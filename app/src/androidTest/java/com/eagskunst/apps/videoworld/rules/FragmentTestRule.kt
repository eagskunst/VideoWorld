package com.eagskunst.apps.videoworld.rules

import android.content.Intent
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.eagskunst.apps.videoworld.VideoWorldTestApp
import com.squareup.picasso.Picasso
import io.mockk.MockKException
import io.mockk.mockkStatic
import org.koin.core.module.Module

/**
 * Created by eagskunst in 18/6/2020.
 */
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
abstract class FragmentTestRule<F : Fragment> :
    ActivityTestRule<FragmentActivity>(FragmentActivity::class.java, true, true) {

    override fun afterActivityLaunched() {
        super.afterActivityLaunched()
        activity.runOnUiThread {
            val fm = activity.supportFragmentManager
            val transaction = fm.beginTransaction()
            createCommonMocks()
            transaction.replace(
                android.R.id.content,
                createFragment()
            ).commit()
        }
    }

    override fun beforeActivityLaunched() {
        super.beforeActivityLaunched()
        val app = InstrumentationRegistry.getInstrumentation()
            .targetContext.applicationContext as VideoWorldTestApp

        app.injectModules(getModule())
    }

    private fun createCommonMocks() {
        mockkStatic(Picasso::class)
    }

    protected abstract fun createFragment(): F

    protected abstract fun getModule(): Module

    fun launch() {
        launchActivity(Intent())
    }
}

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <F : Fragment> createRule(fragment: F, module: Module): FragmentTestRule<F> =
    object : FragmentTestRule<F>() {
        override fun createFragment(): F = fragment
        override fun getModule(): Module = module
    }
