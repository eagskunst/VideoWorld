package com.eagskunst.apps.videoworld.screens

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton
import com.eagskunst.apps.videoworld.R

/**
 * Created by eagskunst in 20/7/2020.
 */
class ClipScreen: Screen<ClipScreen>() {
    val fullScreenBtn = KButton { withId(R.id.fullScreenBtn) }
    val playerView = KView { withId(R.id.playerView) }
    val playbackBtn = KButton { withId(R.id.playbackSpeedBtn) }
    val speedMenuItem = KView { withText("1.5") }
}