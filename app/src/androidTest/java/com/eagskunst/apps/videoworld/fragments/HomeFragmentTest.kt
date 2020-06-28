package com.eagskunst.apps.videoworld.fragments

import android.view.View
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.eagskunst.apps.videoworld.TestValuesUtils
import com.eagskunst.apps.videoworld.app.network.responses.user.UserDataResponse
import com.eagskunst.apps.videoworld.app.network.responses.user.UserResponse
import com.eagskunst.apps.videoworld.common.LiveDataHolder
import com.eagskunst.apps.videoworld.rules.createRule
import com.eagskunst.apps.videoworld.screens.HomeScreen
import com.eagskunst.apps.videoworld.screens.isKeyboardClose
import com.eagskunst.apps.videoworld.ui.fragments.HomeFragment
import com.eagskunst.apps.videoworld.utils.formatInt
import com.eagskunst.apps.videoworld.viewmodels.TwitchViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module

/**
 * Created by eagskunst in 18/6/2020.
 */
private const val VALID_USER_ID1 = "Rubius"
private const val VALID_USER_ID2 = "EvilAFM"
private const val INVALID_USER_ID = "kjsdgknsdgkj"

@RunWith(AndroidJUnit4ClassRunner::class)
class HomeFragmentTest {

    private val liveDataHolder = LiveDataHolder()
    private val fragment = HomeFragment()
    lateinit var twitchViewModel: TwitchViewModel

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
        liveDataHolder.mockForBaseViewModel(twitchViewModel)
        every { twitchViewModel.userData } returns userData

        // Normal answer, instant response
        every { twitchViewModel.getUserByInput(VALID_USER_ID1) }.answers {
            updateUserDataLiveData(userData)
        }

        //Just show progress. Test function should handle the GONE state
        every { twitchViewModel.getUserByInput(VALID_USER_ID2) }.answers {
            liveDataHolder.updateProgress(View.VISIBLE)
        }

        //Invalid user ID, null response
        every { twitchViewModel.getUserByInput(INVALID_USER_ID) }.answers {
            userData.postValue(null)
        }


        //For verify
        this.twitchViewModel = twitchViewModel

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
            nameInput.typeText(VALID_USER_ID1)
            nameInput.pressImeAction()
            isKeyboardClose() // Check is keyboard was closed
            countingTaskExecutorRule.drainTasks(5, TimeUnit.SECONDS)
            streamerNameTv.hasText(VALID_USER_ID1)
            streamerDescp.hasText("Soy streamer")
            streamerCount.hasText("Views: ${5000.formatInt()}")
        }
    }

    @Test
    fun whenWritingAName_AndPressingTheSearchButton_AssertTextChanges() {
        onScreen<HomeScreen> {
            nameInput.typeText(VALID_USER_ID1)
            searchBtn.click()
            isKeyboardClose() // Check is keyboard was closed
            countingTaskExecutorRule.drainTasks(5, TimeUnit.SECONDS)
            streamerNameTv.hasText(VALID_USER_ID1)
            streamerDescp.hasText("Soy streamer")
            streamerCount.hasText("Views: ${5000.formatInt()}")
        }
    }

    @Test
    fun whenWritingAName_AndPressingTheSearchButton_ThenShowTheProgressBar_ThenHideTheProgressBar(){
        onScreen<HomeScreen> {
            nameInput.typeText(VALID_USER_ID2)
            searchBtn.click()

            // Fetching
            progressBar.isVisible()
            streamerCardContainer.isGone()

            //Update progress
            liveDataHolder.updateProgress(View.GONE)

            // Then
            progressBar.isGone()
            streamerCardContainer.isVisible()
        }
    }

    @Test
    fun whenWritingAValidName_UpdateUI_ThenWriteAnInvalidName_AssertCardTextsAreEmpty() {
        onScreen<HomeScreen> {
            //Valid user ID
            nameInput.typeText(VALID_USER_ID1)
            searchBtn.click()
            countingTaskExecutorRule.drainTasks(5, TimeUnit.SECONDS)

            //UI updates correctly with new data
            streamerNameTv.hasText(VALID_USER_ID1)
            streamerDescp.hasText("Soy streamer")
            streamerCount.hasText("Views: ${5000.formatInt()}")
            streamerImg.isVisible()

            //Invalid user ID
            nameInput.clearText()
            nameInput.typeText(INVALID_USER_ID)
            searchBtn.click()
            countingTaskExecutorRule.drainTasks(5, TimeUnit.SECONDS)

            //UI updates correctly, showing empty texts
            streamerNameTv.hasEmptyText()
            streamerDescp.hasEmptyText()
            streamerCount.hasEmptyText()
            streamerImg.isInvisible()
        }
    }

    @Test
    fun whenReceivingAnEmptyInput_ThenViewModelFunctionShouldNotBeInvoke() {
        onScreen<HomeScreen> {
            searchBtn.click()
            verify(exactly = 0) {
                twitchViewModel.getUserByInput("")
            }
        }
    }

    private fun updateUserDataLiveData(userData: MutableLiveData<UserDataResponse>) {
        val userResponse: UserResponse = mockk()
        every { userResponse.displayName } returns VALID_USER_ID1
        every { userResponse.profileImageUrl } returns "https://example.com/1.jpg"
        every { userResponse.description } returns "Soy streamer"
        every { userResponse.viewCount } returns 5000
        userData.postValue(UserDataResponse(listOf(userResponse)))
    }
}
