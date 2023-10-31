package com.dicoding.stroyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.stroyapp.paging.Repository
import com.dicoding.stroyapp.response.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel(private val pref: Repository) : ViewModel() {
    val user: LiveData<List<ListStoryItem>> = pref.user

    fun getStoryWithLocation(token: String) {
        viewModelScope.launch {
            pref.getStoryWithLocation(token)
        }
    }

    fun getToken(): LiveData<String> {
        return pref.getToken()
    }
}