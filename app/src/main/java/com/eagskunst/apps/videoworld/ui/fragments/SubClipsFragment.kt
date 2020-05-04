package com.eagskunst.apps.videoworld.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.app.models.PlayerState
import com.eagskunst.apps.videoworld.databinding.FragmentClipsBinding
import com.eagskunst.apps.videoworld.ui.view_holders.clipInfoView
import com.eagskunst.apps.videoworld.utils.DownloadState
import com.eagskunst.apps.videoworld.utils.base.BaseFragment
import com.eagskunst.apps.videoworld.viewmodels.PlayerViewModel
import timber.log.Timber

/**
 * Created by eagskunst in 3/5/2020.
 */
class SubClipsFragment: BaseFragment<FragmentClipsBinding>(R.layout.fragment_clips) {

    override val bindingFunction: (view: View) -> FragmentClipsBinding
        get() = FragmentClipsBinding::bind

    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clipsToolbar.visibility = View.GONE
        playerViewModel.playerStateLiveData.observe(viewLifecycleOwner, Observer { state ->
            if(state == null)
                return@Observer
            Timber.d("Binding new player state into sub clips list")
            bindClips(state)
            binding.clipsRv.smoothScrollToPosition(state.currentPosition)
        })
    }

    override fun onResume() {
        super.onResume()
        playerViewModel.playerStateLiveData.value?.let {
            binding.clipsRv.scrollToPosition(it.currentPosition)
        }
    }

    private fun bindClips(playerState: PlayerState) {
        binding.clipsRv.withModels {
            playerState.clipsList.forEach { clip ->
                //Binding clip into view holder
                clipInfoView {
                    id(clip.id)
                    clip(clip)
                    viewClick { _, _, _, position ->
                        playerViewModel.changePlayerState(
                            playerState.copy(
                                currentPosition = position
                            )
                        )
                    }
                    val color =
                        if (clip == playerState.clipsList[playerState.currentPosition]) R.color.colorAccent
                        else R.color.colorDefaultBg

                    backgroundColor(color)
                    downloadClick {  _ -> /**/ }
                }
            }
        }
    }
}