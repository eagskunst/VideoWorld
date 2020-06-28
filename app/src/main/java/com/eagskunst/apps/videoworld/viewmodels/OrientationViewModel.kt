package com.eagskunst.apps.videoworld.viewmodels

import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.eagskunst.apps.videoworld.utils.base.BaseViewModel

/**
 * Created by eagskunst in 3/5/2020.
 */
class OrientationViewModel : BaseViewModel() {

    private val _configData = MutableLiveData<Configuration>()
    val configData = _configData as LiveData<Configuration>

    fun changeConfiguration(configuration: Configuration?) {
        _configData.value = configuration
    }
}
