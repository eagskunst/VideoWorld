package com.eagskunst.apps.videoworld.app.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.eagskunst.apps.videoworld.DownloadApi
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.app.di.factories.ChildWorkerFactory
import com.eagskunst.apps.videoworld.app.network.api.TwitchDownloadApi
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

/**
 * Created by eagskunst in 30/4/2020.
 */
class VideoDownloadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val downloadApi: TwitchDownloadApi): CoroutineWorker(context, params) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    companion object {
        private const val PROGRESS_MAX = 100
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "VWCH1"
        private const val FILENAME = "download-video-videoworld.mp4"
    }

    override suspend fun doWork(): Result {
        val url = inputData.getString("URL") ?: return Result.failure()
        setForeground(createForegroundInfo(currentProgress = 0))
        return download(url, FILENAME)
    }

    private fun createForegroundInfo(progress: String = "Downloading file",
                                     currentProgress: Int): ForegroundInfo {
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

        val notificationBuilder = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setProgress(PROGRESS_MAX, currentProgress, false)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_rewind)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .setChannelId(CHANNEL_ID)

        return ForegroundInfo(NOTIFICATION_ID, notificationBuilder.build())
    }

    private suspend fun download(url: String, outputFile: String): Result {
        return withContext(Dispatchers.IO){
            try {
                Timber.d("Starting download of $outputFile from $url")

                val responseBody = downloadApi.downloadVideo(url).body()
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

                        //Update notification
                        val currentProgress = currentProgress(length.toDouble(), bytesRed.toDouble())
                        setForeground(
                            createForegroundInfo(currentProgress = currentProgress)
                        )
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

    private fun currentProgress(length: Double, bytesRed: Double): Int {
        Timber.d("Updating notification. Length: $length, bytesRed: $bytesRed")
        val progressDouble = (bytesRed/length)*100.0
        return if(progressDouble.toInt() < 1) 1 else progressDouble.toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val name = "Main channel"
        val descriptionText = "Download channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        notificationManager.createNotificationChannel(mChannel)
    }

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory
}