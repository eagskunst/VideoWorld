package com.eagskunst.apps.videoworld.screens


import androidx.test.espresso.matcher.ViewMatchers
import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton
import com.eagskunst.apps.videoworld.R

/**
 * Created by eagskunst in 5/7/2020.
 */
class AddCommentScreen: Screen<AddCommentScreen>() {
    val commentEt = KEditText {
        withMatcher(ViewMatchers.withHint(R.string.write_your_comment_here_hint))
    }
    val sendBtn = KButton { withId(R.id.sendComment) }
}