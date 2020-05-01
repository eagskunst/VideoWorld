@file:Suppress("UNCHECKED_CAST")

package com.eagskunst.apps.videoworld.utils

import android.app.Activity
import android.content.Context
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import com.eagskunst.apps.videoworld.app.di.component.ComponentProvider
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import androidx.lifecycle.ViewModelProvider

/**
 * Created by eagskunst in 1/5/2020.
 */

fun SimpleExoPlayer.changeSpeed(speed: Float){
    setPlaybackParameters(PlaybackParameters(speed))
}

fun SimpleExoPlayer.updatePosition(newPosition: Int){
    seekTo(currentPosition + newPosition)
}

fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

val Activity.injector get() = (application as ComponentProvider).appComponent

inline fun <reified T : ViewModel> FragmentActivity.viewModel(
    crossinline provider: () -> T) = viewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) = provider() as T
    }
}

inline fun <reified T : ViewModel> Fragment.activityViewModel(
    crossinline provider: () -> T) = activityViewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) = provider() as T
    }
}

fun AppCompatActivity.hideKeyboard() {
    val view = this.currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

}
