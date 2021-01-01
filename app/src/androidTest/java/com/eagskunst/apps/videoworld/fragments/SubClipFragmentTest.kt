package com.eagskunst.apps.videoworld.fragments

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.agoda.kakao.screen.Screen.Companion.idle
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.eagskunst.apps.videoworld.ConditionRobot
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.app.models.PlayerState
import com.eagskunst.apps.videoworld.builders.clipResponse
import com.eagskunst.apps.videoworld.common.LiveDataHolder
import com.eagskunst.apps.videoworld.rules.createRule
import com.eagskunst.apps.videoworld.screens.ClipsListScreen
import com.eagskunst.apps.videoworld.screens.recycler_items.ClipItem
import com.eagskunst.apps.videoworld.ui.fragments.SubClipsFragment
import com.eagskunst.apps.videoworld.viewmodels.PlayerViewModel
import com.google.android.exoplayer2.Player
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import java.util.UUID

/**
 * Created by eagskunst in 15/7/2020.
 */
class SubClipFragmentTest {

    private val liveDataHolder = LiveDataHolder()
    val fragment = SubClipsFragment()
    val clipsSize = 20
    var listener: Player.EventListener? = null
    lateinit var playerViewModel: PlayerViewModel

    val dates = listOf(
        "2020-06-10T12:33:17Z",
        "2018-05-09T12:33:17Z",
        "2017-04-08T00:34:17Z",
        "2010-01-01T12:33:17Z"
    )

    @get:Rule
    val fragmentRule = createRule(fragment, module {
        single<PlayerViewModel>(override = true) {
            val playerViewModel = mockPlayerViewModel()
            playerViewModel
        }
    })


    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()


    private fun mockPlayerViewModel(): PlayerViewModel {
        playerViewModel = mockk<PlayerViewModel>(relaxed = true)
        val clips = (0 until clipsSize).map {
            clipResponse {
                id = UUID.randomUUID().toString()
                title = "Clip $it"
                viewCount = (it + 1) * 10000
                createdAt = dates[it % 4]
            }
        }
        val playerState = PlayerState(clips, 0)
        val stateLiveData = MutableLiveData<PlayerState>(playerState)
        every { playerViewModel.playerStateLiveData } returns stateLiveData
        every { playerViewModel.changePlayerState(playerState.copy(currentPosition = 2)) } answers {
            stateLiveData.postValue(playerState.copy(currentPosition = 2))
        }
        every { playerViewModel.changePlayerState(playerState.copy(currentPosition = 1)) } answers {
            stateLiveData.postValue(playerState.copy(currentPosition = 1))
        }
        every { playerViewModel.changePlayerState(playerState.copy(currentPosition = clipsSize - 1)) } answers {
            stateLiveData.postValue(playerState.copy(currentPosition = clipsSize - 1))
        }

        every { playerViewModel.createPlayerListener(playerState) } answers  {
            callOriginal()
        }

        listener = playerViewModel.createPlayerListener(playerState)

        return playerViewModel.also {
            liveDataHolder.mockForBaseViewModel(it)
        }
    }

    @Before
    fun setup() {
        ConditionRobot().waitUntil {
            ::playerViewModel.isInitialized
        }
    }

    //SubClipsFragment uses the same layout as ClipListFragment, so we reuse the screen
    @Test
    fun whenInitialFragmentState_AssertToolbarIsGone_AndFirstChildIsSelected() {
        onScreen<ClipsListScreen> {
            toolbar {
                isGone()
            }
            recycler {
                firstChild<ClipItem> {
                    hasBackgroundColor(R.color.colorAccent)
                }
            }
        }
    }

    @Test
    fun whenClickingOnThirdChild_AssertThirdChildIsSelected_AndFirstIsNot() {
        onScreen<ClipsListScreen> {
            recycler {
                childAt<ClipItem>(2) {
                    click()
                    idle(duration = 500)
                    hasBackgroundColor(R.color.colorAccent)
                }
                firstChild<ClipItem> {
                    hasBackgroundColor(R.color.colorDefaultBg)
                }
            }
        }
    }

    @Test
    fun scrollToLastChild_clickOnIt_AssertLastChildIsSelected_AndFirstIsNot() {
        onScreen<ClipsListScreen> {
            recycler {
                scrollTo(clipsSize - 1)
                childAt<ClipItem>(clipsSize - 1) {
                    click()
                    idle(duration = 500)
                    hasBackgroundColor(R.color.colorAccent)
                }
                firstChild<ClipItem> {
                    hasBackgroundColor(R.color.colorDefaultBg)
                }
            }
        }
    }

    @Test
    fun whenFirstClipIsSelected_AndTheListenerUpdates_AssertSecondClipsIsSelected() {
        onScreen<ClipsListScreen> {
            recycler {
                firstChild<ClipItem> {
                    hasBackgroundColor(R.color.colorAccent)
                }
                childAt<ClipItem>(1) {
                    hasBackgroundColor(R.color.colorDefaultBg)
                }
                listener!!.onPlayerStateChanged(true, Player.STATE_ENDED)

                idle(duration = 500)
                firstChild<ClipItem> {
                    hasBackgroundColor(R.color.colorDefaultBg)
                }
                childAt<ClipItem>(1) {
                    hasBackgroundColor(R.color.colorAccent)
                }
            }
        }
        listener = null
    }

}