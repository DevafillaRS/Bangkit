package com.dicoding.stroyapp.api

import com.dicoding.stroyapp.response.DetailResponse
import com.dicoding.stroyapp.response.LoginResponse
import com.dicoding.stroyapp.response.RegisterResponse
import com.dicoding.stroyapp.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int = 0
    ): StoryResponse

    @GET("stories/{id}")
    fun getDetailStory(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<DetailResponse>

    @GET("stories")
    fun getStoryWithLocation(
        @Header("Authorization") authorization: String,
        @Query("location") location: Int = 1
    ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<RegisterResponse>
}