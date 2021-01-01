package com.eagskunst.apps.videoworld.app.network.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * Created by eagskunst in 30/4/2020.
 */
interface TwitchDownloadApi {
    @Streaming
    @GET
    suspend fun downloadVideo(@Url fileUrl: String): Response<ResponseBody>
}
