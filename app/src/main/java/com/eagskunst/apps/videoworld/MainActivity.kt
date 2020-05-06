package com.eagskunst.apps.videoworld

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.eagskunst.apps.videoworld.app.workers.VideoDownloadWorker
import com.eagskunst.apps.videoworld.databinding.ActivityMainBinding
import com.eagskunst.apps.videoworld.utils.changeSpeed
import com.eagskunst.apps.videoworld.utils.updatePosition
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.material.button.MaterialButton
import org.koin.android.ext.android.inject
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val player: SimpleExoPlayer by lazy {
        SimpleExoPlayer.Builder(this).build()
    }

    private val urls = listOf(
        "https://clips-media-assets2.twitch.tv/34928803440-offset-3170.mp4",
        "https://clips-media-assets2.twitch.tv/AT-cm%7C386828697.mp4")

    private val speeds = listOf(
        0.5f,
        1.0f,
        1.5f)

    var isPortrait = true

    private val dsFactory by inject<DataSource.Factory>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.playerView.player = player

        var currentUrl = urls[0]
        var videoSource = createVideoSource(currentUrl)

        player.prepare(videoSource)

        with(binding){
            changeClipBtn.setOnClickListener {
                currentUrl = if(currentUrl == urls[0])
                    urls[1]
                else
                    urls[0]
                videoSource = createVideoSource(currentUrl)
                player.prepare(videoSource)
            }

            downloadBtn.setOnClickListener {
                val url = getUrlFromCurrentClip(currentUrl)
                Timber.d("Url: $url")
                val data = workDataOf(
                    VideoDownloadWorker.VIDEO_URL to url,
                    VideoDownloadWorker.DESIRED_FILENAME to "twitch-clip.mp4"
                )

                val request = OneTimeWorkRequestBuilder<VideoDownloadWorker>()
                    .setInputData(data)
                    .build()

                WorkManager.getInstance(this@MainActivity)
                    .enqueueUniqueWork(VideoDownloadWorker.WORK_NAME, ExistingWorkPolicy.REPLACE, request)
            }

            bindPlayerViews(playerView)
        }

    }

    //TODO: Should be in a ViewModel for testing
    private fun getUrlFromCurrentClip(currentUrl: String): String? {
        return Regex("[^/]+\$").find(currentUrl)?.value
    }

    private fun bindPlayerViews(playerView: PlayerView) {
        val playBackSpeedBtn = playerView.findViewById<MaterialButton>(R.id.playbackSpeedBtn) ?: null
        playBackSpeedBtn?.setOnClickListener {
            val popupMenu = createPopupMenu(it)
            popupMenu.show()
        }

        val fullScreenBtn = playerView.findViewById<MaterialButton>(R.id.fullScreenBtn) ?: null
        fullScreenBtn?.setOnClickListener {
            changeOrientation(it as MaterialButton)
        }

        val forwardBtn = playerView.findViewById<MaterialButton>(R.id.btnForward) ?: null
        val rewindBtn = playerView.findViewById<MaterialButton>(R.id.btnRewind) ?: null
        forwardBtn?.setOnClickListener { player.updatePosition(5000) }
        rewindBtn?.setOnClickListener { player.updatePosition(-5000) }
    }

    private fun changeOrientation(fullScreenBtn: MaterialButton) {
        val orientation = if (isPortrait){
            fullScreenBtn.icon = ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_exit_black)
            isPortrait = false
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        else {
            fullScreenBtn.icon = ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_black)
            isPortrait = true
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        requestedOrientation = orientation
    }

    private fun createVideoSource(currentUrl: String): ProgressiveMediaSource {
        return ProgressiveMediaSource.Factory(dsFactory)
            .createMediaSource(Uri.parse(currentUrl))
    }

    override fun onResume() {
        player.playWhenReady = true
        super.onResume()
    }

    override fun onPause() {
        player.playWhenReady = false
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        player.release()
    }

    fun createPopupMenu(view: View) = PopupMenu(view.context, view).apply {
        speeds.forEach { menu.add("$it") }
        setOnMenuItemClickListener { item ->
            player.changeSpeed(item.title.toString().toFloat())
            (view as MaterialButton?)?.text = item.title
            true
        }
    }
}