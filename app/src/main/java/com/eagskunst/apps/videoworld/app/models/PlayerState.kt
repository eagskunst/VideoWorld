package com.eagskunst.apps.videoworld.app.models

/**
 * Created by eagskunst in 3/5/2020.
 */
data class PlayerState(
    val clipsList: List<ClipInfo>,
    val currentPosition: Int
) {
    val maxPosition: Int = clipsList.size
}

data class ClipInfo(
    val id: String,
    val url: String
)
