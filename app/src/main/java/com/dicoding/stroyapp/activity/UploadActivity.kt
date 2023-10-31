package com.dicoding.stroyapp.activity

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.stroyapp.databinding.ActivityUploadBinding
import com.dicoding.stroyapp.factory.ViewModelFactory
import com.dicoding.stroyapp.reduceFileImage
import com.dicoding.stroyapp.rotateFile
import com.dicoding.stroyapp.uriToFile
import com.dicoding.stroyapp.viewmodel.UploadViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private val uploadViewModel: UploadViewModel by viewModels { viewModelFactory }
    private lateinit var viewModelFactory: ViewModelFactory

    private var getFile: File? = null

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan izin.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAlert()

        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.buttonUpload.setOnClickListener {
            uploadStory()
        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun setupAlert() {
        uploadViewModel.isCreated.observe(this) {
            if (it) {
                Toast.makeText(this@UploadActivity, "Upload Berhasil", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else Toast.makeText(this@UploadActivity, "Upload Gagal", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun setupViewModel() {
        viewModelFactory = ViewModelFactory.getInstance(this)

        uploadViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pbUpload.visibility = View.VISIBLE
        } else {
            binding.pbUpload.visibility = View.GONE
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@UploadActivity)
                getFile = myFile
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    private fun uploadStory() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description = binding.edAddDescription.text.toString()

            val desc = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            showLoading(true)
            uploadViewModel.getToken().observe(this) {
                showLoading(false)
                if (it != "") {
                    uploadViewModel.uploadStory(it, imageMultipart, desc)
                }
            }
        } else {
            Toast.makeText(
                this@UploadActivity,
                "Silakan masukkan item yang kosong !",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}