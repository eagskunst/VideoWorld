package com.eagskunst.apps.videoworld.app.models

import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse

/**
 * Created by eagskunst in 3/5/2020.
 */
data class PlayerState(
    val clipsList: List<ClipResponse>,
    val currentPosition: Int
) {
    val maxPosition: Int = clipsList.size
}
