package com.eagskunst.apps.videoworld.screens

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.test.platform.app.InstrumentationRegistry
import com.agoda.kakao.screen.Screen

/**
 * Created by eagskunst in 27/6/2020.
 */

fun <T: Screen<T>> Screen<T>.isKeyboardClose() {
    val inputMethodManager = InstrumentationRegistry.getInstrumentation().targetContext.getSystemService(
        Context.INPUT_METHOD_SERVICE) as InputMethodManager

    assert(!inputMethodManager.isAcceptingText) {
        "The Keyboard is currently accepting text."
    }
}