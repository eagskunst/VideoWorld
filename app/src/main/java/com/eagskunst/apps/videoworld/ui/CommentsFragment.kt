package com.eagskunst.apps.videoworld.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.databinding.FragmentCommentsBinding
import com.eagskunst.apps.videoworld.progressBar
import com.eagskunst.apps.videoworld.utils.base.BaseFragment

class CommentsFragment : BaseFragment<FragmentCommentsBinding>(R.layout.fragment_comments) {

    override val bindingFunction: (view: View) -> FragmentCommentsBinding
        get() = FragmentCommentsBinding::bind

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.commentsRv.withModels {
            (0..19).forEach {
                progressBar {
                    id("progress$it")
                }
            }
        }
    }
}
