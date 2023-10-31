package com.dicoding.stroyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.stroyapp.paging.Repository
import com.dicoding.stroyapp.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val pref: Repository) : ViewModel() {

    val isLoading: LiveData<Boolean> = pref.isLoading
    val user: LiveData<List<ListStoryItem>> = pref.user

    fun story(token: String): LiveData<PagingData<ListStoryItem>> {
        return pref.getStory(token).cachedIn(viewModelScope)
    }

    fun getToken(): LiveData<String> {
        return pref.getToken()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}