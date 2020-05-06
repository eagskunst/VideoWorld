package com.eagskunst.apps.videoworld.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.app.models.PlayerState
import com.eagskunst.apps.videoworld.databinding.FragmentClipsBinding
import com.eagskunst.apps.videoworld.ui.view_holders.ClipInfoView
import com.eagskunst.apps.videoworld.ui.view_holders.clipInfoView
import com.eagskunst.apps.videoworld.utils.base.BaseFragment
import com.eagskunst.apps.videoworld.viewmodels.PlayerViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

/**
 * Created by eagskunst in 3/5/2020.
 */
class SubClipsFragment: BaseFragment<FragmentClipsBinding>(R.layout.fragment_clips) {

    override val bindingFunction: (view: View) -> FragmentClipsBinding
        get() = FragmentClipsBinding::bind

    private val playerViewModel: PlayerViewModel by sharedViewModel()

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

    /**
     * Automatically scroll to the video being displayed. Even if user navigated to another app
     */
    override fun onResume() {
        super.onResume()
        playerViewModel.playerStateLiveData.value?.let {
            binding.clipsRv.scrollToPosition(it.currentPosition)
        }
    }

    /**
     * Binds every clip's [ClipInfoView] variables.
     * The holds view's click listener updates the player state.
     * @param playerState: The current state of the ExoPlayer view.
     */
    private fun bindClips(playerState: PlayerState) {
        binding.clipsRv.withModels {
            playerState.clipsList.forEach { clip ->
                //Binding clip into view holder
                clipInfoView {
                    id(clip.id)
                    clip(clip)
                    viewClick { _, _, _, position ->
                        if(playerState.currentPosition != position){
                            playerViewModel.changePlayerState(
                                playerState.copy(
                                    currentPosition = position
                                )
                            )
                        }
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