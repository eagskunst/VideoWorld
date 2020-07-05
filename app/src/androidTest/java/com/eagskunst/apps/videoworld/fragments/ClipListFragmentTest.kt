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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.eagskunst.apps.videoworld.ConditionRobot
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.VideoWorldTestApp
import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import com.eagskunst.apps.videoworld.app.network.responses.clips.Pagination
import com.eagskunst.apps.videoworld.app.network.responses.clips.UserClipsResponse
import com.eagskunst.apps.videoworld.app.network.responses.user.UserDataResponse
import com.eagskunst.apps.videoworld.app.network.responses.user.UserResponse
import com.eagskunst.apps.videoworld.builders.clipResponse
import com.eagskunst.apps.videoworld.common.LiveDataHolder
import com.eagskunst.apps.videoworld.getOrAwaitValue
import com.eagskunst.apps.videoworld.rules.createRule
import com.eagskunst.apps.videoworld.screens.ClipsListScreen
import com.eagskunst.apps.videoworld.screens.recycler_items.ClipItem
import com.eagskunst.apps.videoworld.screens.recycler_items.EmptinessItem
import com.eagskunst.apps.videoworld.ui.fragments.ClipsListFragment
import com.eagskunst.apps.videoworld.utils.Constants
import com.eagskunst.apps.videoworld.utils.DownloadState
import com.eagskunst.apps.videoworld.utils.WorkStateHandler
import com.eagskunst.apps.videoworld.viewmodels.DownloadViewModel
import com.eagskunst.apps.videoworld.viewmodels.PlayerViewModel
import com.eagskunst.apps.videoworld.viewmodels.TwitchViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.android.get
import org.koin.dsl.module
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by eagskunst in 28/6/2020.
 */
private val STREAMER_NAME = "Rubius"
private val STREAMER_ID = "MockID"
private val INVALID_STREAMER_ID = "InvalidId"

@RunWith(AndroidJUnit4ClassRunner::class)
class ClipListFragmentTest {

    private val liveDataHolder = LiveDataHolder()
    private val fragment = ClipsListFragment()
    lateinit var twitchViewModel: TwitchViewModel
    lateinit var downloadViewModel: DownloadViewModel
    lateinit var workStateHandler: WorkStateHandler
    lateinit var clips: List<ClipResponse>

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
            workStateHandler = mockk<WorkStateHandler>(relaxed = true)
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
        twitchViewModel = mockk<TwitchViewModel>(relaxed = true)
        val userResponse = mockk<UserResponse>(relaxed = true)

        every { userResponse.id } returns STREAMER_ID

        every { userResponse.displayName } returns STREAMER_NAME
        every { userResponse.id } returns STREAMER_ID
        val userDataResponse = UserDataResponse(listOf(userResponse))
        val userLiveData = MutableLiveData<UserDataResponse>(userDataResponse)
        val clipsLiveData = MutableLiveData<UserClipsResponse>()
        clips = (0..3).map {
            clipResponse {
                id = UUID.randomUUID().toString()
                title = "Clip $it"
                viewCount = (it + 1) * 10000
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

        every { twitchViewModel.getUserClips(INVALID_STREAMER_ID) }.answers {
            clipsLiveData.postValue(UserClipsResponse(listOf(), Pagination("0")))
        }

        liveDataHolder.mockForBaseViewModel(twitchViewModel)
        return twitchViewModel
    }

    private fun mockDownloadViewModel(): DownloadViewModel {
        val viewModel = DownloadViewModel("path")

        downloadViewModel = viewModel

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
                firstChild<ClipItem> {
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

    @Test
    fun whenFragmentWithViews_checkDownloadStateChanges() {
        val screen = onScreen<ClipsListScreen> {}
        changeToDownloading(screen)
        changeToDownloaded(screen)
        changeToNotDownloaded(screen)
    }

    private fun changeToDownloading(screen: ClipsListScreen) {
        screen.recycler {
            firstChild<ClipItem> {
                downloadBtn.perform { click() }
                val clip = clips[0]
                assert(downloadViewModel.getDownloadStateForClip(clip) == DownloadState.DOWNLOADING) {
                    "The current download state for $clip is ${downloadViewModel.getDownloadStateForClip(
                        clip
                    )}"
                }
            }
        }
    }

    private fun changeToDownloaded(screen: ClipsListScreen) {
        screen.recycler {
            firstChild<ClipItem> {
                val clip = clips[0]
                downloadViewModel.updateDownloadedVideosList(clip)
                downloadBtn.perform { click() } //Request a model build to the EpoxyRv
                assert(downloadViewModel.getDownloadStateForClip(clip) == DownloadState.DOWNLOADED) {
                    "The current download state for $clip is ${downloadViewModel.getDownloadStateForClip(
                        clip
                    )}"
                }
            }
        }
    }

    private fun changeToNotDownloaded(screen: ClipsListScreen) {
        screen.recycler {
            firstChild<ClipItem> {
                downloadBtn.perform { click() }
                val clip = clips[0]
                assert(downloadViewModel.getDownloadStateForClip(clip) == DownloadState.NOT_DOWNLOADED) {
                    "The current download state for $clip is ${downloadViewModel.getDownloadStateForClip(
                        clip
                    )}"
                }
            }
        }
    }

    @Test
    fun whenFragmentWithNullVideosList_AssertEmptinessViewIsShown() {
        ConditionRobot().waitUntil {
            ::twitchViewModel.isInitialized
        }
        twitchViewModel.getUserClips(INVALID_STREAMER_ID)
        onScreen<ClipsListScreen> {
            recycler {
                firstChild<EmptinessItem> {
                    container.isVisible()
                }
            }
        }
    }

}