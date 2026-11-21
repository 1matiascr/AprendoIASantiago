package com.example.aprendoiasantiago.network

import com.example.aprendoiasantiago.model.Player
import com.example.aprendoiasantiago.model.QrPoint
import com.example.aprendoiasantiago.model.VideoCapsule
import retrofit2.Response
import retrofit2.http.*

interface BackendApiService {

    // 1. Jugadores
    @GET("api/players")
    suspend fun getPlayers(): List<Player>

    @POST("api/players")
    suspend fun createPlayer(@Body player: Player): Response<Player>

    @PUT("api/players/{id}")
    suspend fun updatePlayer(@Path("id") id: Int, @Body player: Player): Response<Player>

    // 2. Locaciones QR
    @GET("api/points")
    suspend fun getPoints(): List<QrPoint>

    @POST("api/points")
    suspend fun createPoint(@Body point: QrPoint): Response<QrPoint>

    @PUT("api/points/{id}")
    suspend fun updatePoint(@Path("id") id: Int, @Body point: QrPoint): Response<QrPoint>

    @DELETE("api/points/{id}")
    suspend fun deletePoint(@Path("id") id: Int): Response<Unit>

    // 3. Videos / Shorts
    @GET("api/videos")
    suspend fun getAllVideos(): List<VideoCapsule>

    @GET("api/videos/point/{pointId}")
    suspend fun getVideosByPoint(@Path("pointId") pointId: Int): List<VideoCapsule>

    @POST("api/videos")
    suspend fun createVideo(@Body video: VideoCapsule): Response<VideoCapsule>

    @DELETE("api/videos/{id}")
    suspend fun deleteVideo(@Path("id") id: Int): Response<Unit>
}