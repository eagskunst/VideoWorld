package com.eagskunst.apps.videoworld.utils

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * Created by eagskunst in 3/5/2020.
 */

@BindingAdapter("app:backgroundColor")
fun setViewBackgroundColor(view: View, color: Int){
    view.setBackgroundColor(color)
}