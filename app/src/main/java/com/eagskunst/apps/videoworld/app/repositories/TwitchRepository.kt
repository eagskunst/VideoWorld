package com.eagskunst.apps.videoworld.app.repositories

import com.eagskunst.apps.videoworld.utils.RemoteErrorEmitter
import dagger.Reusable
import javax.inject.Inject

/**
 * Created by eagskunst in 1/5/2020.
 */
@Reusable
class TwitchRepository @Inject constructor(private val remoteRepository: TwitchRemoteRepository) {

    suspend fun getUserByName(userName: String,
                              remoteErrorEmitter: RemoteErrorEmitter
    ) = remoteRepository.getUserByName(userName, remoteErrorEmitter)

    suspend fun getUserClips(userId: String,
                             remoteErrorEmitter: RemoteErrorEmitter
    ) = remoteRepository.getUserClips(userId, remoteErrorEmitter)

}