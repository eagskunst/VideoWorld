package com.eagskunst.apps.videoworld.utils

import android.content.Context
import androidx.preference.PreferenceManager
import com.eagskunst.apps.videoworld.app.di.scopes.AppScope
import javax.inject.Inject

/**
 * Created by eagskunst in 3/5/2020.
 */
@AppScope
class UserPreferences @Inject constructor(context: Context) {

    private val prefManager = PreferenceManager.getDefaultSharedPreferences(context)

    fun isVideoSaved(videoFileName: String) = prefManager.getBoolean(videoFileName, false)

}