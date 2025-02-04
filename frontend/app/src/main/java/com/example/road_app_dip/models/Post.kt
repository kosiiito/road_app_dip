package com.example.road_app_dip.models
data class ImageBuffer(val data: List<Int>)

data class Post(
    val _id: String,
    val image: ImageBuffer? = null,
    val description: String = "",
    val user: Users,
    val createdAt: String,
    val updatedAt: String
)

