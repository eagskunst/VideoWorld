package com.eagskunst.apps.videoworld.ui

import android.view.View
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.databinding.FragmentClipsBinding
import com.eagskunst.apps.videoworld.utils.base.BaseFragment

class ClipsFragment : BaseFragment<FragmentClipsBinding>(R.layout.fragment_clips) {

    override val bindingFunction: (view: View) -> FragmentClipsBinding
        get() = FragmentClipsBinding::bind

}
