package com.example.road_app_dip.network

import com.example.road_app_dip.models.Users
import com.example.road_app_dip.models.Post
import com.example.road_app_dip.models.Location
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ApiInterface {
    @POST("auth/register")
    suspend fun registerUser(@Body users: Users): Response<Users>
    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun loginUser(@Body user: Users): Response<Map<String, String>>

    @Multipart
    @POST("upload")
    suspend fun uploadPost(
        @Part("caption") caption: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<Post>

    @GET("feed")
    suspend fun getFeed(): Response<List<Post>>

    @POST("map/add")
    suspend fun addLocation(@Body location: Location): Response<Location>

    @GET("map")
    suspend fun getLocations(): Response<List<Location>>
}
