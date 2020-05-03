package com.eagskunst.apps.videoworld.viewmodels

import android.content.Context
import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import com.eagskunst.apps.videoworld.utils.DownloadState
import com.eagskunst.apps.videoworld.utils.base.BaseViewModel
import java.io.File
import java.util.*

/**
 * Created by eagskunst in 3/5/2020.
 */
class DownloadViewModel: BaseViewModel() {

    private var downloadedVideosList = listOf<ClipResponse>()
    private val downloadingVideosList = mutableListOf<ClipResponse>()
    val downloadWorksIds = mutableListOf<UUID>()

    fun updateDownloadedVideosList(clips: List<ClipResponse>, filesDirPath: String) {
        downloadedVideosList = clips.filter {
            File("${filesDirPath}/${it.getClipFilename()}").exists()
        }
    }

    fun updateDownloadedVideosList(clip: ClipResponse) {
        downloadedVideosList = downloadingVideosList.toMutableList().apply {
            add(clip)
        }
    }

    fun getDownloadStateForClip(clip: ClipResponse): Int {
        return when {
            downloadedVideosList.contains(clip) -> DownloadState.DOWNLOADED
            downloadingVideosList.contains(clip) -> DownloadState.DOWNLOADING
            else -> DownloadState.NOT_DOWNLOADED
        }
    }

    fun addVideoToDownloadList(clip: ClipResponse) = downloadingVideosList.add(clip)
    fun removeVideoFromDownloadList(clip: ClipResponse) = downloadingVideosList.remove(clip)

    fun deleteClipInFiles(context: Context, clip: ClipResponse) {
        File("${context.filesDir}${clip.getClipFilename()}").delete()
        downloadedVideosList = downloadedVideosList.toMutableList().apply {
            remove(clip)
        }
    }

    //TODO: Should be in a VewModel for testing
    fun getDownloadUrlOfClip(clip: ClipResponse): String? {
        return Regex("[^/]+\$").find(clip.getClipUrl())?.value
    }

}