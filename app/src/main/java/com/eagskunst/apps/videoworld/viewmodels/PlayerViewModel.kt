package com.eagskunst.apps.videoworld.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.eagskunst.apps.videoworld.app.models.PlayerState
import com.eagskunst.apps.videoworld.utils.base.BaseViewModel

/**
 * Created by eagskunst in 3/5/2020.
 */
class PlayerViewModel: BaseViewModel() {

    private val _playerStateLiveData = MutableLiveData<PlayerState>()
    val playerStateLiveData = _playerStateLiveData as LiveData<PlayerState>

    fun changePlayerState(state: PlayerState?){
        _playerStateLiveData.value = state
    }

}