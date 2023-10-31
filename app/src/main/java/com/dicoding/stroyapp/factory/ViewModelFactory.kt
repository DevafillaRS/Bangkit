package com.dicoding.stroyapp.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.stroyapp.paging.Injection
import com.dicoding.stroyapp.paging.Repository
import com.dicoding.stroyapp.viewmodel.*

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val pref: Repository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(pref) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }

            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(pref) as T
            }

            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
                UploadViewModel(pref) as T
            }

            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(pref) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
        }
    }
}