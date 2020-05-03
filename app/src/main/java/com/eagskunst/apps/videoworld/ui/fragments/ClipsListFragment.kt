package com.eagskunst.apps.videoworld.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.app.models.PlayerState
import com.eagskunst.apps.videoworld.app.network.responses.clips.UserClipsResponse
import com.eagskunst.apps.videoworld.clipInfo
import com.eagskunst.apps.videoworld.databinding.FragmentClipsBinding
import com.eagskunst.apps.videoworld.progressBar
import com.eagskunst.apps.videoworld.utils.activityViewModel
import com.eagskunst.apps.videoworld.utils.base.BaseFragment
import com.eagskunst.apps.videoworld.utils.injector
import com.eagskunst.apps.videoworld.utils.setDivider
import com.eagskunst.apps.videoworld.viewmodels.PlayerViewModel
import com.eagskunst.apps.videoworld.viewmodels.TwitchViewModel

class ClipsListFragment : BaseFragment<FragmentClipsBinding>(R.layout.fragment_clips) {

    override val bindingFunction: (view: View) -> FragmentClipsBinding
        get() = FragmentClipsBinding::bind

    private val twitchViewModel: TwitchViewModel by activityViewModel {
        injector.twitchViewModel
    }

    private val playerViewModel: PlayerViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clipsToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        twitchViewModel.userData.observe(viewLifecycleOwner, Observer { data ->
            if(data != null && data.dataList.isNotEmpty()){
                val streamerName = data.dataList[0].displayName
                binding.clipsToolbar.title = "$streamerName clips"
            }
        })

        twitchViewModel.userClips.observe(viewLifecycleOwner, Observer { res ->
            buildRecyclerView(binding, res)
        })

        if(!twitchViewModel.clipsListExists())
            twitchViewModel.getUserClips(twitchViewModel.currentUserId())
    }

    private fun buildRecyclerView(binding: FragmentClipsBinding, res: UserClipsResponse?) {
        binding.clipsRv.withModels {
            if(res == null)
                progressBar { id("progress") }
            else {
                res.clipResponseList.forEach { clip ->
                    clipInfo {
                        id(clip.id)
                        clip(clip)
                        backgroundColor(R.color.colorDefaultBg)
                        viewClick { _, _, _, position ->

                            playerViewModel.changePlayerState(
                                PlayerState(
                                    res.clipResponseList, position
                                )
                            )

                            findNavController().navigate(R.id.action_clipsFragment_to_clipFragment)
                        }
                    }
                }
            }
        }
        binding.clipsRv.setDivider(R.drawable.divider)
    }

    override fun onDetach() {
        super.onDetach()
        playerViewModel.changePlayerState(null)
    }
}
