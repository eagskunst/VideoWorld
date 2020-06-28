package com.eagskunst.apps.videoworld.app.repositories

import com.eagskunst.apps.videoworld.utils.RemoteErrorEmitter

/**
 * Created by eagskunst in 1/5/2020.
 */
class TwitchRepository(private val remoteRepository: TwitchRemoteRepository) {

    suspend fun getUserByName(
        userName: String,
        remoteErrorEmitter: RemoteErrorEmitter
    ) = remoteRepository.getUserByName(userName, remoteErrorEmitter)

    suspend fun getUserClips(
        userId: String,
        remoteErrorEmitter: RemoteErrorEmitter
    ) = remoteRepository.getUserClips(userId, remoteErrorEmitter)

    suspend fun authUser(remoteErrorEmitter: RemoteErrorEmitter) = remoteRepository.getAuthToken(remoteErrorEmitter)
}
