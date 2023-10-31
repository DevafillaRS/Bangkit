package com.dicoding.stroyapp.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.stroyapp.databinding.ActivityRegisterBinding
import com.dicoding.stroyapp.factory.ViewModelFactory
import com.dicoding.stroyapp.user.EmailUser
import com.dicoding.stroyapp.user.NameUser
import com.dicoding.stroyapp.user.PasswordUser
import com.dicoding.stroyapp.viewmodel.RegisterViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "setting")

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val regisViewModel: RegisterViewModel by viewModels { viewModelFactory }
    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var pwUser: PasswordUser
    private lateinit var emailUser: EmailUser
    private lateinit var nameUser: NameUser


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nameUser = binding.edRegisterName
        emailUser = binding.edRegisterEmail
        pwUser = binding.edRegisterPassword
        val cbPW = binding.cbShowPassword

        cbPW.setOnCheckedChangeListener { _, isChecked ->
            pwUser.transformationMethod =
                if (isChecked) HideReturnsTransformationMethod.getInstance()
                else PasswordTransformationMethod.getInstance()
        }

        setupView()
        setupViewModel()
        setupAction()
        animationSec()
    }

    @Suppress("DEPRECATION")
    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        viewModelFactory = ViewModelFactory.getInstance(this)

        regisViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            setupAlert()
            val name = nameUser.text.toString()
            val email = emailUser.text.toString()
            val password = pwUser.text.toString()
            when {
                name.isEmpty() -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Nama tidak boleh kosong",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                email.isEmpty() -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Email tidak boleh kosong",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                password.length < 8 -> {
                    pwUser.error = "Format Password Salah"
                }

                else -> regisViewModel.regisUser(name, email, password)
            }
        }
    }

    private fun setupAlert() {
        regisViewModel.isCreated.observe(this) {
            showLoading(true)
            if (it) {
                AlertDialog.Builder(this).apply {
                    showLoading(false)
                    setTitle("Selamat !")
                    setMessage("Akun anda sudah jadi, silakan mengekspresikan diri anda !")
                    setPositiveButton("Berikutnya") { _, _ ->
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    create()
                    show()
                }
            } else {
                AlertDialog.Builder(this).apply {
                    showLoading(false)
                    setTitle("INVALID")
                    setPositiveButton("Isi kembali") { _, _ ->
                        val intent = Intent(this@RegisterActivity, RegisterActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    create()
                    show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pbRegis.visibility = View.VISIBLE
        } else {
            binding.pbRegis.visibility = View.GONE
        }
    }

    private fun animationSec() {
        val img = ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 1f).setDuration(800)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(300)
        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(300)
        val mName =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(300)
        val mEmail =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val pw = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(300)
        val mPw =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val cb = ObjectAnimator.ofFloat(binding.cbShowPassword, View.ALPHA, 1f).setDuration(300)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(img, title, name, mName, email, mEmail, pw, mPw, cb, signup)
            start()
        }
    }
}