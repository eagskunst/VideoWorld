package com.eagskunst.apps.videoworld.viewmodels

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import com.eagskunst.apps.videoworld.app.network.responses.clips.UserClipsResponse
import com.eagskunst.apps.videoworld.app.network.responses.user.UserDataResponse
import com.eagskunst.apps.videoworld.app.repositories.TwitchRepository
import com.eagskunst.apps.videoworld.utils.base.BaseViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by eagskunst in 1/5/2020.
 */
class TwitchViewModel @Inject constructor(private val repository: TwitchRepository): BaseViewModel() {

    private val _userData = MutableLiveData<UserDataResponse>()
    val userData = _userData as LiveData<UserDataResponse>
    private val _userClips = MutableLiveData<UserClipsResponse?>()
    val userClips = _userClips as LiveData<UserClipsResponse?>

    fun currentUserId() = userData.value?.dataList?.get(0)?.id ?: ""
    fun clipsListExists() = userClips.value != null

    fun getUserByInput(input: String){
        viewModelScope.launch {
            _progressVisibility.value = View.VISIBLE
            _userData.value = repository.getUserByName(input, this@TwitchViewModel)
            _progressVisibility.value = View.GONE
        }
    }

    /**
     * @param userId The ID of the Streamer whose clips are gonna be fetch. If null, resets
     * If empty, sets the [userClips] value to null.
     */
    fun getUserClips(userId: String){
        if(userId.isEmpty()) {
            _userClips.postValue(null)
            return
        }
        if(userId == currentUserId()) {
            _userClips.postValue(_userClips.value)
        }

        viewModelScope.launch {
            _userClips.value = repository.getUserClips(userId, this@TwitchViewModel)
        }
    }
}