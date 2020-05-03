package com.eagskunst.apps.videoworld.ui

import android.os.Bundle
import android.view.View
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.databinding.FragmentClipsBinding
import com.eagskunst.apps.videoworld.utils.base.BaseFragment

/**
 * Created by eagskunst in 3/5/2020.
 */
class SubClipsFragment: BaseFragment<FragmentClipsBinding>(R.layout.fragment_clips) {

    override val bindingFunction: (view: View) -> FragmentClipsBinding
        get() = FragmentClipsBinding::bind

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clipsToolbar.visibility = View.GONE
    }
}