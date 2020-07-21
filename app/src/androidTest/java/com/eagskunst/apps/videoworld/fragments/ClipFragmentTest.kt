package com.eagskunst.apps.videoworld.fragments

import android.content.res.Configuration
import android.view.WindowManager
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.agoda.kakao.screen.Screen.Companion.idle
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.eagskunst.apps.videoworld.ConditionRobot
import com.eagskunst.apps.videoworld.common.LiveDataHolder
import com.eagskunst.apps.videoworld.rules.createRule
import com.eagskunst.apps.videoworld.screens.ClipScreen
import com.eagskunst.apps.videoworld.ui.fragments.ClipFragment
import com.eagskunst.apps.videoworld.utils.isInPortrait
import com.eagskunst.apps.videoworld.viewmodels.DownloadViewModel
import com.eagskunst.apps.videoworld.viewmodels.OrientationViewModel
import com.eagskunst.apps.videoworld.viewmodels.PlayerViewModel
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.upstream.DataSource
import io.mockk.every
import io.mockk.mockk
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module

/**
 * Created by eagskunst in 20/7/2020.
 */
class ClipFragmentTest {
    private val liveDataHolder = LiveDataHolder()
    private val fragment = ClipFragment()
    private val dependencies = mutableMapOf<String, Any>()

    @get:Rule
    val fragmentRule = createRule(fragment, module(createdAtStart = true) {
        single(override = true) {
            val vm = mockk<PlayerViewModel>(relaxed = true)
            every { vm.playerStateLiveData } returns MutableLiveData()
            dependencies["playervm"] = vm
            vm
        }
        single(override = true) {
            val vm = OrientationViewModel()
            dependencies["orientationvm"] = vm
            vm
        }
        single(override = true) {
            val vm = mockk<DownloadViewModel>(relaxed = true)
            dependencies["downloadvm"] = vm
            vm
        }
        single(override = true) {
            val dsf = mockk<DataSource.Factory>()
            dependencies["dsf"] = dsf
            dsf
        }
        single(override = true) {
            val sep = SimpleExoPlayer
                .Builder(fragment.requireContext())
                .build()
            dependencies["sep"] = sep
            sep
        }
    })


    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    @Before
    fun setup() {
        ConditionRobot().waitUntil {
            dependencies.size == 5
        }
    }

    @Test
    fun afterPressingFullScreenButton_AssertScreenOrientationIsLandscape_AndWindowFlagChanges() {
        onScreen<ClipScreen> {
            fullScreenBtn.click()
            val orientationVm = dependencies["orientationvm"] as OrientationViewModel
            val config = Configuration()
            config.orientation = Configuration.ORIENTATION_LANDSCAPE
            fragmentRule.activity.runOnUiThread {
                orientationVm.changeConfiguration(config)
            }
            idle(1000)
            assert(!fragmentRule.activity.isInPortrait())
            playerView.matches {
                this.isCompletelyDisplayed()
            }
            assert(fragmentRule.activity.window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN == 0)
        }
    }

    @Test
    fun clickMultipleTimesTheScreenButton_AssertOrientationChange() {
        onScreen<ClipScreen> {
            var inPortrait = false
            for (i in 0 until 5) {
                fullScreenBtn.click()
                idle(duration = 500)
                assertThat(fragmentRule.activity.isInPortrait(), `is`(inPortrait))
                inPortrait = !inPortrait
            }
        }
    }

    @Test
    fun whenClickingOnPlaybackSpeedButton_AssertPopUpMenuComesUp_AndPlayerSpeedChanges() {
        onScreen<ClipScreen> {
            playbackBtn.click()
            speedMenuItem.isVisible()
            speedMenuItem.click()
            val player = dependencies["sep"] as SimpleExoPlayer
            assertThat(player.playbackParameters.speed , `is`(1.5f))
        }
    }

}