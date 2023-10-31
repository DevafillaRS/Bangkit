package com.dicoding.stroyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.stroyapp.paging.Repository
import com.dicoding.stroyapp.response.Story
import kotlinx.coroutines.launch

class DetailViewModel(private val pref: Repository) : ViewModel() {
    val detailStory: LiveData<Story> = pref.detailStory
    val isLoading: LiveData<Boolean> = pref.isLoading

    fun getDetailStory(token: String, id: String) {
        viewModelScope.launch {
            pref.getDetailStory(token, id)
        }
    }

    fun getToken(): LiveData<String> {
        return pref.getToken()
    }
}