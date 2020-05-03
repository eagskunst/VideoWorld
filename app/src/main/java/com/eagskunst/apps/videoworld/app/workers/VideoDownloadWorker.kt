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
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.app.di.factories.ChildWorkerFactory
import com.eagskunst.apps.videoworld.app.network.api.TwitchDownloadApi
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        const val WORK_NAME = "DownloadWork"
        const val VIDEO_URL = "URL"
        const val DESIRED_FILENAME = "filename"
        const val CLIP_TITLE = "clip_title"
        private const val PROGRESS_MAX = 100
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "VWCH1"
    }

    override suspend fun doWork(): Result {
        Timber.d("Clip title in inputData: ${inputData.getString(CLIP_TITLE)}")
        val url = inputData.getString(VIDEO_URL) ?: return Result.failure()
        val filename = inputData.getString(DESIRED_FILENAME) ?: return  Result.failure()
        val clipTitle = inputData.getString(CLIP_TITLE) ?: "Downloading clip"
        val foregroundInfo = createForegroundInfo(
            currentProgress = 0,
            clipTitle = clipTitle,
            indeterminateProgress = true)
        setForeground(foregroundInfo)
        return download(url, filename, clipTitle)
    }

    private fun createForegroundInfo(progress: String = "Downloading file",
                                     currentProgress: Int,
                                     onGoing: Boolean = true,
                                     indeterminateProgress: Boolean = false,
                                     clipTitle: String): ForegroundInfo {

        val id = "DownloadClip-VideoWorld"
        val cancel = "Cancel"
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(clipTitle)
            .setTicker(clipTitle)
            .setProgress(PROGRESS_MAX, currentProgress, indeterminateProgress)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_rewind)
            .setOngoing(onGoing)
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .setChannelId(CHANNEL_ID)

        return ForegroundInfo(NOTIFICATION_ID, notificationBuilder.build())
    }

    private suspend fun download(url: String, outputFile: String, clipTitle: String): Result {
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
                            createForegroundInfo(currentProgress = currentProgress, clipTitle = clipTitle)
                        )
                    }
                    output.flush()
                }

                Timber.d("Video downloaded and saved.")
                responseBody?.close()
                setForeground(createForegroundInfo(currentProgress = 100, onGoing = false, clipTitle = clipTitle))
                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
    }

    private fun currentProgress(length: Double, bytesRed: Double): Int {
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