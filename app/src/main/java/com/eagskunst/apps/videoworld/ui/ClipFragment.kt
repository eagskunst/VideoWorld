package com.eagskunst.apps.videoworld.ui

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter

import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.databinding.FragmentClipBinding
import com.eagskunst.apps.videoworld.utils.*
import com.eagskunst.apps.videoworld.utils.base.BaseFragment
import com.eagskunst.apps.videoworld.viewmodels.OrientationViewModel
import com.eagskunst.apps.videoworld.viewmodels.TwitchViewModel
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayoutMediator

const val CLIP_URL = "URL"
const val CLIP_ID = "CLIP_ID"
class ClipFragment : BaseFragment<FragmentClipBinding>(R.layout.fragment_clip) {

    override val bindingFunction: (view: View) -> FragmentClipBinding
        get() = FragmentClipBinding::bind

    private val twitchViewModel: TwitchViewModel by activityViewModel { injector.viewModel }
    private val orientationViewModel: OrientationViewModel by activityViewModels()
    private val dsFactory by lazy { injector.dataSourceFactory }
    private val player: SimpleExoPlayer by lazy {
        SimpleExoPlayer.Builder(requireContext()).build()
    }
    //Playback speeds for ExoPlayer
    private val speeds = listOf(
        0.5f,
        1.0f,
        1.5f)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playerView.player = player
        val currentUrl = arguments?.getString(CLIP_URL) ?: ""
        val videoSource = createVideoSource(currentUrl)
        player.prepare(videoSource)
        player.playWhenReady = true
        val adapter = InnerFragmentsAdapter(childFragmentManager, listOf(
            SubClipsFragment(),
            CommentsFragment()
        ))
        binding.clipFragmentsPager.adapter = adapter
        TabLayoutMediator(binding.clipTabLayout, binding.clipFragmentsPager) { tab, position ->
            tab.text = if(position == 0)
                "Clips"
            else
                "Comments"
        }.attach()
        bindPlayerViews(binding.playerView)
    }

    override fun onPause() {
        super.onPause()
        player.playWhenReady = false
    }

    override fun onDetach() {
        super.onDetach()
        player.stop()
        player.release()
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    /**
     * Binds and set clicks of the views inside the custom controller view of [PlayerView]
     * @param playerView: The player that holds the custom controls layout.
     */
    private fun bindPlayerViews(playerView: PlayerView) {
        val playBackSpeedBtn = playerView.findViewById<MaterialButton>(R.id.playbackSpeedBtn) ?: null
        playBackSpeedBtn?.setOnClickListener {
            val popupMenu = createPopupMenu(it)
            popupMenu.show()
        }

        val fullScreenBtn = playerView.findViewById<MaterialButton>(R.id.fullScreenBtn) ?: null

        fullScreenBtn?.setOnClickListener {
            requireActivity().requestedOrientation = if(requireActivity().isInPortrait()){
                 ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            else
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        orientationViewModel.configData.observe(viewLifecycleOwner, Observer { config ->
            if(config != null){
                updatePlayerView(fullScreenBtn, config.orientation)
                updateWindowMode(config.orientation)
            }
        })

        val forwardBtn = playerView.findViewById<MaterialButton>(R.id.btnForward) ?: null
        val rewindBtn = playerView.findViewById<MaterialButton>(R.id.btnRewind) ?: null
        forwardBtn?.setOnClickListener { player.updatePosition(5000) }
        rewindBtn?.setOnClickListener { player.updatePosition(-5000) }

        playerView.findViewById<ImageView?>(R.id.backBtn)?.setOnClickListener { findNavController().navigateUp() }
    }

    private fun createVideoSource(currentUrl: String): ProgressiveMediaSource {
        return ProgressiveMediaSource.Factory(dsFactory)
            .createMediaSource(Uri.parse(currentUrl))
    }

    /**
     * Change full screen button and params on orientation change
     * @param fullScreenBtn: The button that handles the orientation changes
     * @param orientation: The current [ActivityInfo] screen orientation
     */
    private fun updatePlayerView(fullScreenBtn: MaterialButton?, orientation: Int) {
        val params = binding.playerView.layoutParams as ConstraintLayout.LayoutParams
        if(orientation == Configuration.ORIENTATION_LANDSCAPE){
            params.height = FrameLayout.LayoutParams.MATCH_PARENT
            fullScreenBtn?.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_fullscreen_exit_black)
        }
        else {
            params.height = 300.px
            fullScreenBtn?.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_fullscreen_black)
        }
        binding.playerView.layoutParams = params
    }

    private fun updateWindowMode(orientation: Int){
        if(orientation == ActivityInfo.SCREEN_ORIENTATION_USER)
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        else
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }


    /**
     * Creates a [PopupMenu] for the playbacks selection.
     * @param view: The [MaterialButton] that owns the ClickListener to show this pop up menu.
     */
    private fun createPopupMenu(view: View) = PopupMenu(view.context, view).apply {
        speeds.forEach { menu.add("$it") }
        setOnMenuItemClickListener { item ->
            player.changeSpeed(item.title.toString().toFloat())
            (view as MaterialButton?)?.text = item.title
            true
        }
    }

    inner class InnerFragmentsAdapter(manager: FragmentManager, private val fragments: List<Fragment>): FragmentStateAdapter(manager, lifecycle){
        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

        override fun getItemCount(): Int {
            return fragments.size
        }
    }
}
