package com.eagskunst.apps.videoworld.utils

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eagskunst.apps.videoworld.utils.ErrorType
import com.eagskunst.apps.videoworld.utils.RemoteErrorEmitter
import com.eagskunst.apps.videoworld.utils.ScreenState

/**
 * Created by eagskunst in 1/12/2019.
 */
abstract class BaseViewModel : ViewModel(), RemoteErrorEmitter {

    protected val _progressVisibility = MutableLiveData(View.GONE)
    protected val _screenState = MutableLiveData<ScreenState>()
    protected val _errorType =  MutableLiveData<ErrorType>()
    protected val _errorMessage = MutableLiveData<String>()

    val progressVisibility = _progressVisibility as LiveData<Int>
    val screenState = _screenState as LiveData<ScreenState>
    val errorType = _errorType as LiveData<ErrorType>
    val errorMessage = _errorMessage as LiveData<String>

    override fun onError(errorType: ErrorType) {
        _errorType.postValue(errorType)
    }

    override fun onError(msg: String) {
        _errorMessage.postValue(msg)
    }

}