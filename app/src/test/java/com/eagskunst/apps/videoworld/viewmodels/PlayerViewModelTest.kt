package com.eagskunst.apps.videoworld.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.eagskunst.apps.videoworld.app.models.PlayerState
import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import com.eagskunst.apps.videoworld.testShared.getOrAwaitValue
import com.google.android.exoplayer2.Player
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by eagskunst in 8/6/2020.
 */
class PlayerViewModelTest {

    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val viewModel = PlayerViewModel()

    @MockK
    lateinit var clips: List<ClipResponse>
    lateinit var firstState: PlayerState
    lateinit var secondState: PlayerState

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        every { clips.size } returns 3

        firstState = PlayerState(clips, 0)
        secondState = PlayerState(clips, 1)
    }

    @Test
    fun assertInitialStateIsNull(){
        assert (
            viewModel.playerStateLiveData.value == null
        )
    }

    @Test
    fun changeState_toFirstState_assertIsEqualToLiveData() {
        viewModel.changePlayerState(firstState)
        val currentState = viewModel.playerStateLiveData.getOrAwaitValue()
        val expectedState = firstState

        assertThat(currentState, `is`(expectedState))
    }


    @Test
    fun changeState_toSecondState_assertIsEqualToLiveData() {
        viewModel.changePlayerState(secondState)
        val currentState = viewModel.playerStateLiveData.getOrAwaitValue()
        val expectedState = secondState

        assertThat(currentState, `is`(expectedState))
    }


    @Test
    fun changeState_withEventListener_toSecondState_assertIsEqualToLiveData() {
        val listener = viewModel.createPlayerListener(secondState)
        listener.onPlayerStateChanged(true, Player.STATE_ENDED)

        val expectedState = secondState.copy(currentPosition = secondState.currentPosition + 1)
        val currentState = viewModel.playerStateLiveData.getOrAwaitValue()

        assertThat(currentState ,`is`(expectedState))
    }

}