package com.eagskunst.apps.videoworld.app.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.app.di.factories.ChildWorkerFactory
import com.eagskunst.apps.videoworld.app.network.api.TwitchDownloadApi
import com.eagskunst.apps.videoworld.utils.DownloadState
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.Streaming
import timber.log.Timber
import java.io.File

import java.io.FileOutputStream

/**
 * Created by eagskunst in 30/4/2020.
 * Creates a download work for a given URL.
 * This will throw an NPE if the parameters [VIDEO_URL] and [DESIRED_FILENAME] are not in the
 * [Data] inputData.
 * This is created as a background work, and show a notification with the current progress.
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
        const val NOTIFICATION_ID = "notification_id"
        const val DOWNLOAD_STATE = "DownloadState"
        private const val PROGRESS_MAX = 100
        private const val CHANNEL_ID = "VWCH1"
    }

    private val notificationId: Int by lazy {
        inputData.getInt(NOTIFICATION_ID, 1)
    }

    override suspend fun doWork(): Result {
        Timber.d("Clip title in inputData: ${inputData.getString(CLIP_TITLE)}")
        val url = inputData.getString(VIDEO_URL)!!
        val filename = inputData.getString(DESIRED_FILENAME)!!
        val clipTitle = inputData.getString(CLIP_TITLE) ?: "Downloading clip"
        val foregroundInfo = createForegroundInfo(
            currentProgress = 0,
            clipTitle = clipTitle,
            indeterminateProgress = true)
        setForeground(foregroundInfo)
        return download(url, filename, clipTitle)
    }

    /**
     * Creates the foreground info for the notification.
     * @param progress: The content text of the notification. "Download file" by default.
     * @param currentProgress: The current progress of the download
     * @param onGoing: Determines if the notification is dismissible. True by default
     * @param indeterminateProgress: Determines if the notification's progress bar is indeterminate. False by default
     * @param clipTitle: The notification's title
     */
    private fun createForegroundInfo(progress: String = "Downloading file",
                                     currentProgress: Int,
                                     onGoing: Boolean = true,
                                     indeterminateProgress: Boolean = false,
                                     clipTitle: String): ForegroundInfo {

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(clipTitle)
            .setTicker(clipTitle)
            .setProgress(PROGRESS_MAX, currentProgress, indeterminateProgress)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_rewind)
            .setOngoing(onGoing)

        return ForegroundInfo(notificationId, notificationBuilder.build())
    }

    /**
     * The code that handles the download process. This uses [Retrofit]'s [Streaming] to make a
     * GET request and retrieve the URL is content (In this worker case, a .mp4 video)
     * The response is used as a InputStream and saved in a FileOutput stream, reading 4096 in each
     * iteration. Each iteration updates the notification with the current progress and writes to the output stream.
     * At the end, close the streams an saves the files.
     * This runs in the [Dispatchers.IO] dispatcher and it's encapsulated inside a try-catch block
     * to catch possible read/write issues.
     * @param url: The file URL suffix. the [TwitchDownloadApi] adds the base URL.
     * @param outputFile: The name of the output file.
     * @param clipTitle: The clip title to show on the notification
     * @return [Result] always returns [Result.success] and the [Data] inside it holds the truth of the download.
     */
    private suspend fun download(url: String, outputFile: String, clipTitle: String): Result {
        return withContext(Dispatchers.IO){
            var responseBody: ResponseBody? = null
            try {
                Timber.d("Starting download of $outputFile from $url")

                responseBody = downloadApi.downloadVideo(url).body()
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
                Result.success(createOutputData(true))
            } catch (e: Exception) {
                e.printStackTrace()
                responseBody?.close()
                Result.success(createOutputData(false))
            }
        }
    }

    /**
     * Creates the output data to sent in [Result.success]
     * @param success: Boolean to check if the video did download.
     * @return [Data] the work output data.
     */
    private fun createOutputData(success: Boolean): Data {
        return Data.Builder()
            .putInt(DOWNLOAD_STATE, if(success) DownloadState.DOWNLOADED else DownloadState.NOT_DOWNLOADED)
            .build()
    }

    /**
     * The progress is calculated based on the downloaded bytes against the total bytes.
     * @param length total bytes of the file.
     * @param bytesRed downloaded bytes.
     */
    private fun currentProgress(length: Double, bytesRed: Double): Int {
        val progressDouble = (bytesRed/length)*100.0
        return if(progressDouble.toInt() < 1) 1 else progressDouble.toInt()
    }

    /**
     * Creates the download notification channel
     */
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