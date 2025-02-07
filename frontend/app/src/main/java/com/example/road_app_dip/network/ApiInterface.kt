package com.example.road_app_dip.network

import com.example.road_app_dip.models.Feed
import com.example.road_app_dip.models.Users
import com.example.road_app_dip.models.Post
import com.example.road_app_dip.models.Location
import com.example.road_app_dip.models.LocationResponse
import com.example.road_app_dip.models.LoginResponse
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ApiInterface {

    @POST("auth/register")
    suspend fun registerUser(@Body users: Users): Response<Users>

    @Headers("Content-Type: application/json")
    @POST("auth/login")
    suspend fun loginUser(@Body user: Users): Response<LoginResponse>


    @Multipart
    @POST("uploads")
    suspend fun uploadPost(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<Post>


    @GET("feed")
    suspend fun getFeed(): Response<Feed>

    @POST("map/add-location")
    suspend fun addLocation(@Body location: Location, @Header("Authorization")token: String): Response<Location>

    @GET("map/locations")
    suspend fun getLocations(@Query("search") query: String): Response<LocationResponse>

}
