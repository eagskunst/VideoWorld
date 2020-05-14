package com.eagskunst.apps.videoworld.app.repositories

import com.eagskunst.apps.videoworld.app.di.SESSION_TOKEN
import com.eagskunst.apps.videoworld.app.network.api.ClipsApi
import com.eagskunst.apps.videoworld.app.network.api.TwitchAuthApi
import com.eagskunst.apps.videoworld.app.network.api.UserApi
import com.eagskunst.apps.videoworld.utils.base.BaseRemoteRepository
import com.eagskunst.apps.videoworld.utils.RemoteErrorEmitter
import dagger.Reusable
import javax.inject.Inject

/**
 * Created by eagskunst in 1/5/2020.
 */
class TwitchRemoteRepository(
    private val userApi: UserApi,
    private val clipsApi: ClipsApi,
    private val authApi: TwitchAuthApi
    ): BaseRemoteRepository() {

    suspend fun getUserByName(userName: String,
                              remoteErrorEmitter: RemoteErrorEmitter) = safeApiCall(remoteErrorEmitter) {
        userApi.getUserByUsername(userName)
    }

    suspend fun getUserClips(userId: String,
                             remoteErrorEmitter: RemoteErrorEmitter) = safeApiCall(remoteErrorEmitter) {
        clipsApi.getClipsByUserId(userId)
    }

    suspend fun getAuthToken(remoteErrorEmitter: RemoteErrorEmitter) {
        val authResponse = safeApiCall(remoteErrorEmitter){ authApi.getAuthToken() }
        SESSION_TOKEN = authResponse?.accessToken ?: ""
    }
}