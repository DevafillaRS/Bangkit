package com.dicoding.stroyapp.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.dicoding.stroyapp.databinding.ActivityLoginBinding
import com.dicoding.stroyapp.factory.ViewModelFactory
import com.dicoding.stroyapp.user.EmailUser
import com.dicoding.stroyapp.user.PasswordUser
import com.dicoding.stroyapp.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels { viewModelFactory }
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var binding: ActivityLoginBinding

    private lateinit var emailUser: EmailUser
    private lateinit var pwUser: PasswordUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailUser = binding.edLoginEmail
        pwUser = binding.edLoginPassword
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

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }

    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            setupAlert()
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Masukkan Email",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                password.length < 8 -> {
                    pwUser.error = "Format Password Salah"
                }

                else -> {
                    loginViewModel.loginUser(email, password, this)
                    loginViewModel.token.observe(this) {
                        if (it != "") {
                            if (it != null) {
                                loginViewModel.saveToken(it)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupAlert() {
        loginViewModel.isLogin.observe(this) {
            showLoading(true)
            if (it) {
                AlertDialog.Builder(this).apply {
                    showLoading(false)
                    setTitle("Selamat!")
                    setMessage("Anda berhasil login.")
                    setPositiveButton("Lanjut") { _, _ ->
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    create()
                    show()
                }
            } else {
                AlertDialog.Builder(this).apply {
                    showLoading(false)
                    setTitle("Invalid !")
                    setMessage("Silakan isi email password dengan benar")
                    setPositiveButton("OK") { _, _ ->
                        val intent = Intent(context, LoginActivity::class.java)
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
            binding.pbLogin.visibility = View.VISIBLE
        } else {
            binding.pbLogin.visibility = View.GONE
        }
    }

    private fun animationSec() {
        val img = ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 1f).setDuration(800)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(300)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(300)
        val mEmail =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val pw = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(300)
        val mPw =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val cb = ObjectAnimator.ofFloat(binding.cbShowPassword, View.ALPHA, 1f).setDuration(300)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially(img, title, message, email, mEmail, pw, mPw, cb, login)
            start()
        }
    }
}