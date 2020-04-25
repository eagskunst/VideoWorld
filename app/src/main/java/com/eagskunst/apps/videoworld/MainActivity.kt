package com.eagskunst.apps.videoworld

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eagskunst.apps.videoworld.databinding.ActivityMainBinding
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class MainActivity : AppCompatActivity() {

    val player: SimpleExoPlayer by lazy {
        SimpleExoPlayer.Builder(this).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.playerView.player = player

        val dsFactory = DefaultDataSourceFactory(this, "VideoWorld")
        val videoSource = ProgressiveMediaSource.Factory(dsFactory)
                .createMediaSource(Uri.parse("https://clips-media-assets2.twitch.tv/AT-cm%7C386828697.mp4"))

        player.prepare(videoSource)
        player.playWhenReady = true

        with(binding){
            btn1.setOnClickListener { player.changeSpeed(0.5f) }
            btn2.setOnClickListener { player.changeSpeed(0.5f) }
            btn3.setOnClickListener { player.changeSpeed(0.5f) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}

fun SimpleExoPlayer.changeSpeed(speed: Float){
    setPlaybackParameters(PlaybackParameters(speed))
}
