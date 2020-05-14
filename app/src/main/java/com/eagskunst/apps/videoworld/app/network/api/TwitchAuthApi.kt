package com.eagskunst.apps.videoworld.app.network.api

import com.eagskunst.apps.videoworld.app.network.responses.auth.AuthTokenResponse
import com.eagskunst.apps.videoworld.utils.ApiKeys
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by eagskunst in 13/5/2020.
 */
interface TwitchAuthApi {

    @POST("oauth2/token")
    suspend fun getAuthToken(@Query("client_id") clientId: String = ApiKeys.TWITCH_CLIENT_ID,
                     @Query("grant_type") granType: String = "client_credentials",
                     @Query("client_secret") clientSecret: String = ApiKeys.CLIENT_SECRET
    ) : AuthTokenResponse

}