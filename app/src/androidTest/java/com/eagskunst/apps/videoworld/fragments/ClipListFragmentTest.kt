package com.eagskunst.apps.videoworld.fragments

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.widget.ImageView
import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.MutableLiveData
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.VideoWorldTestApp
import com.eagskunst.apps.videoworld.app.network.responses.clips.Pagination
import com.eagskunst.apps.videoworld.app.network.responses.clips.UserClipsResponse
import com.eagskunst.apps.videoworld.app.network.responses.user.UserDataResponse
import com.eagskunst.apps.videoworld.app.network.responses.user.UserResponse
import com.eagskunst.apps.videoworld.builders.clipResponse
import com.eagskunst.apps.videoworld.common.LiveDataHolder
import com.eagskunst.apps.videoworld.rules.createRule
import com.eagskunst.apps.videoworld.screens.ClipsListScreen
import com.eagskunst.apps.videoworld.ui.fragments.ClipsListFragment
import com.eagskunst.apps.videoworld.utils.Constants
import com.eagskunst.apps.videoworld.utils.DownloadState
import com.eagskunst.apps.videoworld.utils.WorkStateHandler
import com.eagskunst.apps.videoworld.viewmodels.DownloadViewModel
import com.eagskunst.apps.videoworld.viewmodels.PlayerViewModel
import com.eagskunst.apps.videoworld.viewmodels.TwitchViewModel
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import java.util.*

/**
 * Created by eagskunst in 28/6/2020.
 */
private val STREAMER_NAME = "Rubius"
private val STREAMER_ID = "MockID"

@RunWith(AndroidJUnit4ClassRunner::class)
class ClipListFragmentTest {

    private val liveDataHolder = LiveDataHolder()
    private val fragment = ClipsListFragment()
    lateinit var twitchViewModel: TwitchViewModel

    @get:Rule
    val fragmentRule = createRule(fragment, module {
        single(override = true) {
            val twitchViewModel = mockTwitchViewModel()
            twitchViewModel
        }
        single(override = true) {
            val downloadViewModel = mockDownloadViewModel()
            downloadViewModel
        }
        single(override = true) {
            val playerViewModel = mockPlayerViewModel()
            playerViewModel
        }
        single(override = true) {
            val workStateHandler = mockk<WorkStateHandler>(relaxed = true)
            workStateHandler
        }
    })

    val dates = listOf(
        "2020-06-10T12:33:17Z",
        "2018-05-09T12:33:17Z",
        "2017-04-08T00:34:17Z",
        "2010-01-01T12:33:17Z"
    )

    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    private fun mockTwitchViewModel(): TwitchViewModel {
        val twitchViewModel = mockk<TwitchViewModel>(relaxed = true)
        val userResponse = mockk<UserResponse>(relaxed = true)

        every { userResponse.id } returns STREAMER_ID

        every { userResponse.displayName } returns STREAMER_NAME
        every { userResponse.id } returns STREAMER_ID
        val userDataResponse = UserDataResponse(listOf(userResponse))
        val userLiveData = MutableLiveData<UserDataResponse>(userDataResponse)
        val clipsLiveData = MutableLiveData<UserClipsResponse>()
        val clips = (0..3).map {
            clipResponse {
                id = UUID.randomUUID().toString()
                title = "Clip $it"
                viewCount = (it+1) * 10000
                createdAt = dates[it]
            }
        }

        every { twitchViewModel.userData } returns userLiveData
        every { twitchViewModel.userClips } returns clipsLiveData
        every {
            twitchViewModel.currentUserId()
        } returns STREAMER_ID

        every {
            twitchViewModel.clipsListExists()
        } returns false

        every { twitchViewModel.getUserClips(STREAMER_ID) }.answers {
            clipsLiveData.postValue(
                UserClipsResponse(clips, Pagination("0"))
            )
        }

        liveDataHolder.mockForBaseViewModel(twitchViewModel)
        this.twitchViewModel = twitchViewModel
        return twitchViewModel
    }

    private fun mockDownloadViewModel(): DownloadViewModel {
        val viewModel =  mockk<DownloadViewModel>(relaxed = true).also {
            liveDataHolder.mockForBaseViewModel(it)
        }

        every { viewModel.getDownloadStateForClip(any()) } returns DownloadState.NOT_DOWNLOADED

        return viewModel
    }

    private fun mockPlayerViewModel(): PlayerViewModel {
        return mockk<PlayerViewModel>(relaxed = true).also {
            liveDataHolder.mockForBaseViewModel(it)
        }
    }

    @Test
    fun whenInitialFragmentState_AssertToolbarTitleContainsStreamerName() {
        onScreen<ClipsListScreen> {
            toolbar.hasTitle("$STREAMER_NAME clips")
        }
    }

    @Test
    fun whenInitialFragmentState_UpdateWithNewClips_AssertRecyclerViewHasViews() {
        onScreen<ClipsListScreen> {
            recycler {
                isVisible()
                hasSize(dates.size)
                firstChild<ClipsListScreen.ClipItem> {
                    isVisible()

                    titleTv {
                        hasText("Clip 0")
                    }

                    iconIv {
                        isVisible()
                    }

                    uploadDateTv {
                        isVisible()
                        val date = Constants.TWITCH_DATE_SDF.parse(dates[0]) ?: Date()
                        hasText(Constants.GLOBAL_SDF.format(date))
                    }

                    viewCountTv {
                        containsText("K")
                    }

                    downloadBtn {
                        isVisible()
                    }
                }
            }
        }
    }

}



data class Temp(val bitmap: Bitmap)