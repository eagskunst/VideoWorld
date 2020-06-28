package com.eagskunst.apps.videoworld.viewmodels

import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import com.eagskunst.apps.videoworld.utils.DownloadState
import com.eagskunst.apps.videoworld.utils.base.BaseViewModel
import java.io.File

/**
 * Created by eagskunst in 3/5/2020.
 */
class DownloadViewModel(private val filesDirPath: String) : BaseViewModel() {

    private var downloadedVideosList = listOf<ClipResponse>()
    private val downloadingVideosList = mutableListOf<ClipResponse>()

    /**
     * Updates the downloaded video list if the file of the Clip exists.
     * @param clips: The clips from the response.
     */
    fun updateDownloadedVideosList(clips: List<ClipResponse>) {
        downloadedVideosList = clips.filter {
            getClipFile(it).exists()
        }
    }

    /**
     * Adds a given clip [ClipResponse] to the downloadedVideosList
     */
    fun updateDownloadedVideosList(clip: ClipResponse) {
        downloadedVideosList = downloadedVideosList.toMutableList().apply {
            add(clip)
        }
    }

    /**
     * Gets the download state of a clip.
     * @param clip: The clip being evaluated
     * @return Int from [DownloadState]. [DownloadState.DOWNLOADED] if the downloadedVideosList contains the
     * clip. [DownloadState.DOWNLOADING] if the downloadingVideosList contains the clip. Else [DownloadState.NOT_DOWNLOADED]
     */
    fun getDownloadStateForClip(clip: ClipResponse): Int {
        return when (clip) {
            in downloadedVideosList -> DownloadState.DOWNLOADED
            in downloadingVideosList -> DownloadState.DOWNLOADING
            else -> DownloadState.NOT_DOWNLOADED
        }
    }

    fun addVideoToDownloadingList(clip: ClipResponse) = downloadingVideosList.add(clip)

    fun removeVideoFromDownloadingList(clip: ClipResponse) {
        downloadingVideosList.remove(clip)
    }

    /**
     * Deletes the clip files and removes it from the downloaded videos list
     * @param clip: The clip to delete.
     */
    fun deleteClipInFiles(clip: ClipResponse) {
        getClipFile(clip).delete()
        downloadedVideosList = downloadedVideosList.toMutableList().apply {
            remove(clip)
        }
    }

    /**
     * Returns the last substring that comes after the last '/' in it.
     * Eg: google.com/hello will return 'hello'
     * @param clip: The clip that holds the URL.
     * @return The suffix URL.
     */
    fun getDownloadUrlOfClip(clip: ClipResponse): String? {
        return Regex("[^/]+\$").find(clip.getClipUrl())?.value
    }

    /**
     * Returns a [File] of a given clip. The file is created with the context.filesDirPath and the
     * [ClipResponse.getClipFilename]
     * @param clip: The clip of the file
     * @return [File]
     */
    fun getClipFile(clip: ClipResponse) = File("$filesDirPath/${clip.getClipFilename()}")

    /**
     * @param clip: The clip to be shown.
     * @return The File path of the video or the URL to the video file.
     */
    fun getClipUrl(clip: ClipResponse): String {
        return if (downloadedVideosList.contains(clip))
            getClipFile(clip).path
        else
            clip.getClipUrl()
    }
}
