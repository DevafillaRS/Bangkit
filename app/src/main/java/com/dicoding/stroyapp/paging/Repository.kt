package com.dicoding.stroyapp.paging

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.stroyapp.api.ApiConfig
import com.dicoding.stroyapp.api.ApiService
import com.dicoding.stroyapp.preference.UserPreference
import com.dicoding.stroyapp.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository(private val pref: UserPreference, private val apiService: ApiService) {

    private val _detailStory = MutableLiveData<Story>()
    val detailStory: LiveData<Story> = _detailStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _token = MutableLiveData<String?>()
    val mToken: LiveData<String?> = _token

    private val _isLogin = MutableLiveData<Boolean>()
    val isLogin: LiveData<Boolean> = _isLogin

    private val _user = MutableLiveData<List<ListStoryItem>>()
    val user: LiveData<List<ListStoryItem>> = _user

    private val _isCreated = MutableLiveData<Boolean>()
    val isCreated: LiveData<Boolean> = _isCreated

    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }

    fun getStoryWithLocation(token: String) {
        val client = apiService.getStoryWithLocation(authorization = token)
        Log.d(TAG, token)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _user.value = response.body()?.listStory
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getDetailStory(token: String, id: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailStory(token, id)
        client.enqueue(object : Callback<DetailResponse> {
            override fun onResponse(
                call: Call<DetailResponse>,
                response: Response<DetailResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _detailStory.value = response.body()?.story
                } else {
                    Log.d(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun loginUser(email: String, password: String, context: Context) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().loginUser(email, password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _isLogin.value = true
                    _token.value = response.body()?.loginResult?.token.toString()
                } else {
                    _isLogin.value = false
                    Log.e(TAG, "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                Toast.makeText(context, t.message.toString(), Toast.LENGTH_SHORT).show()
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun regisUser(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().registerUser(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _isCreated.value = true
                } else {
                    _isCreated.value = false
                    Log.e(TAG, "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun uploadStory(token: String, file: MultipartBody.Part, desc: RequestBody) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().uploadStory(token, file, desc)
        Log.d(TAG, token)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _isCreated.value = true
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error!!) {
                        Log.d(TAG, "onResponse: ${response.message()}")
                    }
                } else {
                    _isCreated.value = false
                    Log.d(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    suspend fun saveToken(token: String) {
        return pref.saveToken("Bearer $token")
    }

    suspend fun logout() {
        pref.logout()
    }

    companion object {
        private const val TAG = "Repository"

        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            preference: UserPreference,
            apiService: ApiService
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(preference, apiService)
            }.also { instance = it }
    }
}