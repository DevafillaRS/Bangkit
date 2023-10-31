package com.dicoding.stroyapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.stroyapp.paging.Repository
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: Repository) : ViewModel() {
    val isLoading: LiveData<Boolean> = pref.isLoading
    val isLogin: LiveData<Boolean> = pref.isLogin
    val token: LiveData<String?> = pref.mToken

    fun loginUser(email: String, password: String, context: Context) {
        viewModelScope.launch {
            pref.loginUser(email, password, context)
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            pref.saveToken(token)
        }
    }
}