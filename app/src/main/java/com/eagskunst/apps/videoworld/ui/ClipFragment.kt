package com.eagskunst.apps.videoworld.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.databinding.CustomPlaybackViewBinding
import com.eagskunst.apps.videoworld.databinding.FragmentClipBinding
import com.eagskunst.apps.videoworld.utils.activityViewModel
import com.eagskunst.apps.videoworld.utils.base.BaseFragment
import com.eagskunst.apps.videoworld.utils.injector
import com.eagskunst.apps.videoworld.utils.showSnackbar
import com.eagskunst.apps.videoworld.viewmodels.TwitchViewModel
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView

const val CLIP_URL = "URL"
class ClipFragment : BaseFragment<FragmentClipBinding>(R.layout.fragment_clip) {

    override val bindingFunction: (view: View) -> FragmentClipBinding
        get() = FragmentClipBinding::bind

    private val twitchViewModel: TwitchViewModel by activityViewModel { injector.viewModel }
    private val dsFactory by lazy { injector.dataSourceFactory }
    private val player: SimpleExoPlayer by lazy {
        SimpleExoPlayer.Builder(requireContext()).build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playerView.player = player
        val currentUrl = arguments?.getString(CLIP_URL) ?: ""
        val videoSource = createVideoSource(currentUrl)
        player.prepare(videoSource)
        player.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        player.playWhenReady = false
    }

    override fun onDetach() {
        super.onDetach()
        player.stop()
        player.release()
    }

    private fun createVideoSource(currentUrl: String): ProgressiveMediaSource {
        return ProgressiveMediaSource.Factory(dsFactory)
            .createMediaSource(Uri.parse(currentUrl))
    }
}
