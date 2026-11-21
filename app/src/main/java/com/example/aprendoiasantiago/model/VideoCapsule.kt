package com.example.aprendoiasantiago.model

data class VideoCapsule(
    val id: Int = 0,
    val title: String,
    val description: String,
    val videoUrl: String,
    val creatorName: String,
    val pointId: Int
)