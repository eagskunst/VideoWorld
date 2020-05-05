package com.eagskunst.apps.videoworld.utils

import androidx.lifecycle.LifecycleOwner
import androidx.work.*
import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import com.eagskunst.apps.videoworld.app.workers.VideoDownloadWorker
import dagger.Reusable
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by eagskunst in 5/5/2020.
 */
@Reusable
class WorkStateHandler @Inject constructor(private val workManager: WorkManager){


    /**
     * Starts a [VideoDownloadWorker]. All works started here add the [ClipResponse.getClipFilename]
     * as a tag.
     * @param clip: The clip to be downloaded, needed for the [Data] of the [VideoDownloadWorker]
     * @param videoUrl: The clip .mp4 url
     * @return The [UUID] of the created work
     */
    fun startDownloadWork(clip: ClipResponse, videoUrl: String?): UUID {
        val data = workDataOf(
            VideoDownloadWorker.VIDEO_URL to videoUrl,
            VideoDownloadWorker.DESIRED_FILENAME to clip.getClipFilename(),
            VideoDownloadWorker.CLIP_TITLE to clip.title,
            VideoDownloadWorker.NOTIFICATION_ID to clip.viewCount
        )

        val request = OneTimeWorkRequestBuilder<VideoDownloadWorker>()
            .setInputData(data)
            .addTag(clip.getClipFilename())
            .build()

        workManager.enqueue(request)
        Timber.d("Enqueued work with id ${request.id}")

        return request.id
    }

    /**
     * Attach an observer to a work. The work that will be send to [onWorkStateChange] will always be not null.
     * @param workId: The [UUID] of the work to observe
     * @param lifecycleOwner: The lifecycle owner of the entity that wants to observe the work
     * @param [onWorkStateChange]: Higher order function to execute when the [WorkInfo] changes
     */
    fun observeWorkById(workId: UUID, lifecycleOwner: LifecycleOwner, onWorkStateChange:(work: WorkInfo) -> Unit) {
        workManager.getWorkInfoByIdLiveData(workId)
            .observe(lifecycleOwner, androidx.lifecycle.Observer {
                val work = it ?: return@Observer
                onWorkStateChange(work)
            })
    }

    /**
     * Cancels the work attached to a given Clip.
     * @param clip: Holds the Work to cancel's tag from [ClipResponse.getClipFilename]
     */
    fun cancelDownloadWork(clip: ClipResponse){
        workManager.cancelAllWorkByTag(clip.getClipFilename())
    }


}