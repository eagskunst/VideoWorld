@file:Suppress("UNCHECKED_CAST")

package com.eagskunst.apps.videoworld.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkInfo
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.snackbar.Snackbar
import kotlin.math.log

/**
 * Created by eagskunst in 1/5/2020.
 */

fun SimpleExoPlayer.changeSpeed(speed: Float) {
    setPlaybackParameters(PlaybackParameters(speed))
}

fun SimpleExoPlayer.updatePosition(newPosition: Int) {
    seekTo(currentPosition + newPosition)
}

fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

/**
 * Start of functions extracted from Paulo's talk
 */
// val Activity.injector get() = (application as ComponentProvider).appComponent
// val Fragment.injector get() = requireActivity().injector

inline fun <reified T : ViewModel> FragmentActivity.viewModel(
    crossinline provider: () -> T
) = viewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) = provider() as T
    }
}

inline fun <reified T : ViewModel> Fragment.activityViewModel(
    crossinline provider: () -> T
) = activityViewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) = provider() as T
    }
}

inline fun <reified T : ViewModel> Fragment.viewModel(
    crossinline provider: () -> T
) = viewModels<T> {
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) = provider() as T
    }
}

/**
 * Ends of functions extracted from Paulo's talk
 */

fun Activity.hideKeyboard() {
    val view = this.currentFocus
    if (view != null) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
}

fun Fragment.hideKeyboard() {
    requireActivity().hideKeyboard()
}

fun Activity.showSnackbar(msg: String) {
    this.currentFocus?.let {
        Snackbar.make(it, msg, Snackbar.LENGTH_SHORT).show()
    }
}

fun Fragment.showSnackbar(msg: String) {
    view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_SHORT).show() }
}

fun RecyclerView.setDivider(@DrawableRes dividerDrawable: Int) {
    val divider = DividerItemDecoration(
        context,
        DividerItemDecoration.VERTICAL
    )
    val drawable = ContextCompat.getDrawable(
        context,
        dividerDrawable
    )
    drawable?.let {
        divider.setDrawable(drawable)
        addItemDecoration(divider)
    }
}

fun Int.formatInt(): String {
    val digits = log(this.toFloat(), 10.toFloat()).toInt() + 1
    if (digits < 4)
        return this.toString()
    val digitsStr = this.toString()
    if (digits in 4..6) {
        return "${digitsStr[0]}K"
    }

    return "${digitsStr[0]}M"
}

val Int.dp get() = (this /
        Resources.getSystem().displayMetrics.density).toInt()
val Int.px get() = (this *
        Resources.getSystem().displayMetrics.density).toInt()

fun Activity.configuration() = resources.configuration

fun Activity.isInPortrait() = configuration().orientation == Configuration.ORIENTATION_PORTRAIT

val WorkInfo.State.isCancelled get() =
    this == WorkInfo.State.CANCELLED || this == WorkInfo.State.FAILED
