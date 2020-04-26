package com.eagskunst.apps.videoworld.app.network.api

import com.eagskunst.apps.videoworld.app.network.responses.user.UserDataResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by eagskunst in 26/4/2020.
 */
interface UserApi {

    @GET("users")
    suspend fun getUserByUsername(@Query("login") username: String): UserDataResponse

}