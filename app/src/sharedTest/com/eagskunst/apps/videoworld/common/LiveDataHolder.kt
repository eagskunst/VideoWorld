package com.eagskunst.apps.videoworld.common

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.eagskunst.apps.videoworld.utils.ErrorType
import com.eagskunst.apps.videoworld.utils.ScreenState
import com.eagskunst.apps.videoworld.utils.base.BaseViewModel
import io.mockk.every

/**
 * Created by eagskunst in 27/6/2020.
 */
class LiveDataHolder {
    private val progressVisibilityLiveData = MutableLiveData(View.GONE)
    private val screenStateLiveData = MutableLiveData<ScreenState>()
    private val errorTypeLiveData = MutableLiveData<ErrorType>()
    private val errorMessageLiveData = MutableLiveData<String>()

    fun updateProgress(visibility: Int) {
        progressVisibilityLiveData.postValue(visibility)
    }

    fun updateScreenState(state: ScreenState) {
        screenStateLiveData.postValue(state)
    }

    fun updateErrorType(errorType: ErrorType) {
        errorTypeLiveData.postValue(errorType)
    }

    fun updateErrorMessageLiveData(errorMessage: String?) {
        errorMessageLiveData.postValue(errorMessage)
    }

    fun mockForBaseViewModel(viewModel: BaseViewModel) {
        every { viewModel.progressVisibility } returns progressVisibilityLiveData
        every { viewModel.errorType } returns errorTypeLiveData
        every { viewModel.errorMessage } returns errorMessageLiveData
        every { viewModel.screenState } returns screenStateLiveData
    }

}
