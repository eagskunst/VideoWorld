package com.eagskunst.apps.videoworld.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.eagskunst.apps.videoworld.app.models.PlayerState
import com.eagskunst.apps.videoworld.utils.base.BaseViewModel
import com.google.android.exoplayer2.Player
import timber.log.Timber

/**
 * Created by eagskunst in 3/5/2020.
 */
class PlayerViewModel : BaseViewModel() {

    private val _playerStateLiveData = MutableLiveData<PlayerState>()
    val playerStateLiveData = _playerStateLiveData as LiveData<PlayerState>

    fun changePlayerState(state: PlayerState?) {
        _playerStateLiveData.value = state
    }

    /**
     * Creates a new [Player.EventListener] that will automatically update the PlayerState of this ViewModel
     * in order to perform an 'auto-play'
     * @param state: The current player state. Needed to know if the state should be updated.
     * @return [Player.EventListener] The event listener to be attached.
     */
    fun createPlayerListener(state: PlayerState) = object : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Timber.d("New playback state: $playbackState. Current position: ${state.currentPosition}.")
            if (playbackState == Player.STATE_ENDED && state.currentPosition < state.maxPosition - 1) {
                changePlayerState(state.copy(currentPosition = state.currentPosition + 1))
            }
        }
    }
}
