package com.eagskunst.apps.videoworld.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.eagskunst.apps.videoworld.app.network.responses.user.UserDataResponse
import com.eagskunst.apps.videoworld.app.network.responses.user.UserResponse
import com.eagskunst.apps.videoworld.rules.createRule
import com.eagskunst.apps.videoworld.screens.HomeScreen
import com.eagskunst.apps.videoworld.ui.fragments.HomeFragment
import com.eagskunst.apps.videoworld.utils.formatInt
import com.eagskunst.apps.videoworld.viewmodels.TwitchViewModel
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module

/**
 * Created by eagskunst in 18/6/2020.
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class HomeFragmentTest {

    private val twitchViewModel: TwitchViewModel = mockk()
    private val userData = MutableLiveData<UserDataResponse>()
    private val fragment = HomeFragment()

    @get:Rule
    val fragmentRule = createRule(fragment, module {
        single(override = true) {
            twitchViewModel
        }
    })

    @Before
    fun setup() {
        val userResponse: UserResponse = mockk()
        every { userResponse.displayName } returns "Rubius"
        every { userResponse.profileImageUrl } returns ""
        every { userResponse.description } returns "Soy streamer"
        every { userResponse.viewCount } returns 5000
        every { twitchViewModel.getUserClips("Rubius") }.answers {
            userData.value = UserDataResponse(listOf(userResponse))
        }
        every { twitchViewModel.userData } returns userData as LiveData<UserDataResponse>
        every { twitchViewModel.userClips } returns MutableLiveData()
        every { twitchViewModel.getUserClips(any()) } just Runs
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
            //nameInput.pressImeAction()
            searchBtn.click()
            runBlocking { delay(2000) }
            streamerNameTv.hasText("Rubius")
            streamerDescp.hasText("Soy streamer")
            streamerCount.hasText("Views: ${5000.formatInt()}}")
        }
    }

}