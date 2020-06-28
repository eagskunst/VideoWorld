package com.eagskunst.apps.videoworld.app.network.api

import com.eagskunst.apps.videoworld.app.network.responses.clips.UserClipsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by eagskunst in 26/4/2020.
 */

interface ClipsApi {

    @GET("clips")
    suspend fun getClipsByUserId(@Query("broadcaster_id") twitchUserId: String): UserClipsResponse
}
