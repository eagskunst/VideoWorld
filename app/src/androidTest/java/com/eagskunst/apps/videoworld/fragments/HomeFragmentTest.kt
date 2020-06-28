package com.eagskunst.apps.videoworld.fragments

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.eagskunst.apps.videoworld.app.network.responses.user.UserDataResponse
import com.eagskunst.apps.videoworld.app.network.responses.user.UserResponse
import com.eagskunst.apps.videoworld.rules.createRule
import com.eagskunst.apps.videoworld.screens.HomeScreen
import com.eagskunst.apps.videoworld.screens.isKeyboardClose
import com.eagskunst.apps.videoworld.ui.fragments.HomeFragment
import com.eagskunst.apps.videoworld.utils.formatInt
import com.eagskunst.apps.videoworld.viewmodels.TwitchViewModel
import io.mockk.every
import io.mockk.mockk
import java.util.concurrent.TimeUnit
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module

/**
 * Created by eagskunst in 18/6/2020.
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class HomeFragmentTest {

    private val fragment = HomeFragment()

    @get:Rule
    val fragmentRule = createRule(fragment, module {
        single(override = true) {
            val twitchViewModel = mockViewModel()
            twitchViewModel
        }
    })

    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    private fun mockViewModel(): TwitchViewModel {
        val userData = MutableLiveData<UserDataResponse>()
        val twitchViewModel = mockk<TwitchViewModel>(relaxed = true)
        every { twitchViewModel.userData } returns userData
        every { twitchViewModel.getUserByInput("Rubius") }.answers {
            updateUserDataLiveData(userData)
        }

        return twitchViewModel
    }

    @Test
    fun testInitialViewState() {
        onScreen<HomeScreen> {
            streamerNameTv.containsText("")
            streamerCardContainer.isVisible()
            nameInput.hasEmptyText()
            progressBar.isGone()
        }
    }

    @Test
    fun whenWritingAName_AndPressingTheImeAction_AssertTextChanges() {
        onScreen<HomeScreen> {
            nameInput.typeText("Rubius")
            nameInput.pressImeAction()
            isKeyboardClose() // Check is keyboard was closed
            countingTaskExecutorRule.drainTasks(5, TimeUnit.SECONDS)
            streamerNameTv.hasText("Rubius")
            streamerDescp.hasText("Soy streamer")
            streamerCount.hasText("Views: ${5000.formatInt()}")
        }
    }

    private fun updateUserDataLiveData(userData: MutableLiveData<UserDataResponse>) {
        val userResponse: UserResponse = mockk()
        every { userResponse.displayName } returns "Rubius"
        every { userResponse.profileImageUrl } returns "https://example.com/1.jpg"
        every { userResponse.description } returns "Soy streamer"
        every { userResponse.viewCount } returns 5000
        userData.postValue(UserDataResponse(listOf(userResponse)))
    }
}
