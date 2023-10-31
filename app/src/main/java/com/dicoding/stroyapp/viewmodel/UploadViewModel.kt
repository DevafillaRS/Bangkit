package com.dicoding.stroyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.stroyapp.paging.Repository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadViewModel(private val pref: Repository) : ViewModel() {
    val isCreated: LiveData<Boolean> = pref.isCreated
    val isLoading: LiveData<Boolean> = pref.isLoading

    fun uploadStory(token: String, file: MultipartBody.Part, desc: RequestBody) {
        viewModelScope.launch {
            pref.uploadStory(token, file, desc)
        }
    }

    fun getToken(): LiveData<String> {
        return pref.getToken()
    }
}