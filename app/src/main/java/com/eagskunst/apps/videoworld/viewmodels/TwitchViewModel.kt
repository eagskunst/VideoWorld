package com.eagskunst.apps.videoworld.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.eagskunst.apps.videoworld.app.network.responses.user.UserDataResponse
import com.eagskunst.apps.videoworld.app.repositories.TwitchRepository
import com.eagskunst.apps.videoworld.utils.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by eagskunst in 1/5/2020.
 */
class TwitchViewModel @Inject constructor(private val repository: TwitchRepository): BaseViewModel() {

    private val _userData = MutableLiveData<UserDataResponse>()
    val userData = _userData as LiveData<UserDataResponse>

    fun getUserByInput(input: String){
        viewModelScope.launch { repository.getUserByName(input, this@TwitchViewModel) }
    }

}