package com.eagskunst.apps.videoworld

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * Created by eagskunst in 18/6/2020.
 */
class CustomTestRunner: AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, VideoWorldTestApp::class.java.name, context)
    }
}