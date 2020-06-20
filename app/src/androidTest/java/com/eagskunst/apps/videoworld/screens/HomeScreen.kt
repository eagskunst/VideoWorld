package com.eagskunst.apps.videoworld.screens

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.progress.KProgressBar
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView
import com.eagskunst.apps.videoworld.R

/**
 * Created by eagskunst in 18/6/2020.
 */
class HomeScreen: Screen<HomeScreen>() {
    val streamerNameTv = KTextView { withId(R.id.streamerLoginTv) }
    val streamerDescp = KTextView { withId(R.id.streamerDescpTv) }
    val streamerCount = KTextView { withId(R.id.streamerViewCountTv) }
    val streamerCardContainer = KView { withId(R.id.cardContent) }
    val progressBar = KProgressBar { withId(R.id.progressBar) }
    val nameInput = KEditText { withId(R.id.nameInput) }
    val searchBtn = KButton { withId(R.id.searchBtn) }
}