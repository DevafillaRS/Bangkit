package com.dicoding.stroyapp.paging

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.stroyapp.api.ApiConfig
import com.dicoding.stroyapp.preference.UserPreference

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("token")

object Injection {
    fun provideRepository(context: Context): Repository {
        val preferences = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return Repository.getInstance(preferences, apiService)
    }
}