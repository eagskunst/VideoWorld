package com.eagskunst.apps.videoworld.app.repositories

import com.eagskunst.apps.videoworld.app.network.api.ClipsApi
import com.eagskunst.apps.videoworld.app.network.api.TwitchDownloadApi
import com.eagskunst.apps.videoworld.app.network.api.UserApi
import com.eagskunst.apps.videoworld.utils.BaseRemoteRepository
import com.eagskunst.apps.videoworld.utils.RemoteErrorEmitter
import dagger.Reusable
import javax.inject.Inject

/**
 * Created by eagskunst in 1/5/2020.
 */
@Reusable
class TwitchRemoteRepository @Inject constructor(
    private val userApi: UserApi,
    private val clipsApi: ClipsApi): BaseRemoteRepository() {

    suspend fun getUserByName(userName: String,
                              remoteErrorEmitter: RemoteErrorEmitter) = safeApiCall(remoteErrorEmitter) {
        userApi.getUserByUsername(userName)
    }

    suspend fun getUserClips(userId: String,
                             remoteErrorEmitter: RemoteErrorEmitter) = safeApiCall(remoteErrorEmitter) {
        clipsApi.getClipsByUserId(userId)
    }
}