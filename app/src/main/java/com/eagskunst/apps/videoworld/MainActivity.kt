package com.eagskunst.apps.videoworld

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.eagskunst.apps.videoworld.databinding.ActivityMainBinding
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

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

            val forwardBtn = playerView.findViewById<MaterialButton>(R.id.btnForward) ?: null
            val rewindBtn = playerView.findViewById<MaterialButton>(R.id.btnRewind) ?: null
            forwardBtn?.setOnClickListener { player.updatePosition(5000) }
            rewindBtn?.setOnClickListener { player.updatePosition(-5000) }

            downloadBtn.setOnClickListener {
                val data = Data.Builder()
                    .putString("URL", "34928803440-offset-3170.mp4")
                    .build()

                val request = OneTimeWorkRequestBuilder<ClipDownloadWorker>()
                    .setInputData(data)
                    .build()

                WorkManager.getInstance(this@MainActivity)
                    .enqueueUniqueWork("DownloadWork", ExistingWorkPolicy.REPLACE, request)
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

fun SimpleExoPlayer.updatePosition(newPosition: Int){
    seekTo(currentPosition + newPosition)
}

fun Context.toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()


class ClipDownloadWorker(val context: Context, params: WorkerParameters): CoroutineWorker(context, params) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    private val PROGRESS_MAX = 100
    private val NOTIFICATION_ID = 1
    lateinit var notificationBuilder: NotificationCompat.Builder

    override suspend fun doWork(): Result {
        val url = inputData.getString("URL") ?: return Result.failure()
        val outputFile = "twitch-clip.mp4"
        setForeground(createForegroundInfo())
        return download(url, outputFile)
    }

    private fun createForegroundInfo(progress: String = "Downloading file"): ForegroundInfo {
        val id = "DownloadClip-ID-08080"
        val title = "Downloading clip"
        val cancel = "Cancel"
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        notificationBuilder = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setProgress(PROGRESS_MAX, 0, false)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_rewind)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .setChannelId("VWCH1")

        return ForegroundInfo(1, notificationBuilder.build())
    }

    private suspend fun download(url: String, outputFile: String): Result {
        return withContext(Dispatchers.IO){
            try {
                Timber.d("Starting download of $outputFile from $url")
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://clips-media-assets2.twitch.tv/")
                    .build()
                val service = retrofit.create(DownloadApi::class.java)
                val responseBody = service.downloadFile(url).body()
                val input = responseBody?.byteStream()
                val file = File(context.filesDir, outputFile)
                val fos = FileOutputStream(file)
                val length = responseBody?.contentLength() ?: 0
                var bytesRed = 0
                fos.use { output ->
                    val buffer = ByteArray(4 * 1024)
                    var read: Int
                    while ( input!!.read(buffer).also { read = it } != -1 ) {
                        bytesRed += read
                        output.write(buffer, 0, read)
                        updateNotification(length.toDouble(), bytesRed.toDouble())
                    }
                    output.flush()
                }

                Timber.d("Video downloaded and saved.")
                responseBody?.close()
                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
    }

    private suspend fun updateNotification(length: Double, bytesRed: Double) {
        withContext(Dispatchers.Main) {
            Timber.d("Updating notification. Length: $length, bytesRed: $bytesRed")
            val progressDouble = (bytesRed/length)*100.0
            val currentProgress = if(progressDouble.toInt() < 1) 1 else progressDouble.toInt()
            notificationBuilder
                .setProgress(PROGRESS_MAX, currentProgress, false)

            setForeground(ForegroundInfo(NOTIFICATION_ID, notificationBuilder.build()))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val name = "Main channel"
        val descriptionText = "Download channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel("VWCH1", name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager.createNotificationChannel(mChannel)
    }
}

interface DownloadApi {
    @Streaming
    @GET
    suspend fun downloadFile(@Url fileUrl: String): Response<ResponseBody>
}