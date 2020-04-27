package com.eagskunst.apps.videoworld

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.eagskunst.apps.videoworld.databinding.ActivityMainBinding
import com.eagskunst.apps.videoworld.databinding.CustomPlaybackViewBinding
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.material.button.MaterialButton
import java.io.File

class MainActivity : AppCompatActivity() {

    val player: SimpleExoPlayer by lazy {
        SimpleExoPlayer.Builder(this).build()
    }

    val urls = listOf("https://clips-media-assets2.twitch.tv/34928803440-offset-3170.mp4", "https://clips-media-assets2.twitch.tv/AT-cm%7C386828697.mp4")
    val speeds = listOf(0.5f, 1.0f, 1.5f)

    var isPortrait = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.playerView.player = player

        var currentUrl = urls[0]
        val evictor = LeastRecentlyUsedCacheEvictor((100 * 1024 * 1024).toLong())
        val cache = SimpleCache(File(cacheDir, "media"), evictor, ExoDatabaseProvider(this))
        val dsFactory = CacheDataSourceFactory(cache, DefaultDataSourceFactory(this, "VideoWorld"))
        var videoSource = createVideoSource(currentUrl, dsFactory)

        player.prepare(videoSource)
        player.playWhenReady = true

        with(binding){
            changeClipBtn.setOnClickListener {
                currentUrl = if(currentUrl == urls[0])
                    urls[1]
                else
                    urls[0]
                videoSource = createVideoSource(currentUrl, dsFactory)
                player.prepare(videoSource)
            }

            val playBackSpeedBtn = playerView.findViewById<MaterialButton>(R.id.playbackSpeedBtn) ?: null
            playBackSpeedBtn?.setOnClickListener {
                val popupMenu = createPopupMenu(it)
                popupMenu.show()
            }

            val fullScreenBtn = playerView.findViewById<MaterialButton>(R.id.fullScreenBtn) ?: null
            fullScreenBtn?.setOnClickListener {
                val orientation = if (isPortrait){
                    fullScreenBtn.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_fullscreen_exit_black)
                    isPortrait = false
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
                else {
                    fullScreenBtn.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_fullscreen_black)
                    isPortrait = true
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                requestedOrientation = orientation
            }
        }
    }

    private fun createVideoSource(currentUrl: String, dsFactory: DataSource.Factory): ProgressiveMediaSource {
        return ProgressiveMediaSource.Factory(dsFactory)
            .createMediaSource(Uri.parse(currentUrl))
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    fun createPopupMenu(view: View) = PopupMenu(view.context, view).apply {
        val menu = menu
        speeds.forEach { menu.add("$it") }
        setOnMenuItemClickListener { item ->
            player.changeSpeed(item.title.toString().toFloat())
            (view as MaterialButton?)?.text = item.title
            true
        }
    }
}

fun SimpleExoPlayer.changeSpeed(speed: Float){
    setPlaybackParameters(PlaybackParameters(speed))
}

fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
