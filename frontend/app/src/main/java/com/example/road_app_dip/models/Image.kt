package com.example.road_app_dip.models

import java.nio.Buffer
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


data class Image @OptIn(ExperimentalEncodingApi::class) constructor(
    val type: Buffer,
    val data: Base64
)
