package com.dicoding.stroyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.stroyapp.paging.Repository
import kotlinx.coroutines.launch

class RegisterViewModel(private val pref: Repository) : ViewModel() {
    val isLoading: LiveData<Boolean> = pref.isLoading
    val isCreated: LiveData<Boolean> = pref.isCreated

    fun regisUser(name: String, email: String, password: String) {
        viewModelScope.launch {
            pref.regisUser(name, email, password)
        }
    }
}