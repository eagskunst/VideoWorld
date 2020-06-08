package com.eagskunst.apps.videoworld.app.models

import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import com.eagskunst.apps.videoworld.ui.fragments.ClipFragment

/**
 * Created by eagskunst in 3/5/2020.
 * A class that holds all the clip list that are currently being displayed
 * on the [ClipFragment], the current position of the clip being displayed and
 * the maximum position the player can do auto-play.
 */
data class PlayerState(
    val clipsList: List<ClipResponse>,
    val currentPosition: Int
) {

    init {
        require(currentPosition >= 0)
    }

    val maxPosition: Int = clipsList.size
}
