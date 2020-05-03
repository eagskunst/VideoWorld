package com.eagskunst.apps.videoworld.viewmodels

import android.content.Context
import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import com.eagskunst.apps.videoworld.utils.DownloadState
import com.eagskunst.apps.videoworld.utils.base.BaseViewModel
import java.io.File
import java.util.*
import javax.inject.Inject

/**
 * Created by eagskunst in 3/5/2020.
 * Context is only used for getting the filesDir is path. Do not worry about
 * leaks of the variable as is only used in the constructor scope
 */
class DownloadViewModel @Inject constructor(context: Context): BaseViewModel() {


    private val filesDirPath: String = context.filesDir.path
    private var downloadedVideosList = listOf<ClipResponse>()
    private val downloadingVideosList = mutableListOf<ClipResponse>()
    val downloadWorksIds = mutableListOf<UUID>()

    fun updateDownloadedVideosList(clips: List<ClipResponse>) {
        downloadedVideosList = clips.filter {
            getClipFile(it).exists()
        }
    }

    fun updateDownloadedVideosList(clip: ClipResponse) {
        if (getClipFile(clip).exists()){
            downloadedVideosList = downloadedVideosList.toMutableList().apply {
                add(clip)
            }
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
    fun removeVideoFromDownloadList(clip: ClipResponse) {
        if (getClipFile(clip).exists())
            downloadingVideosList.remove(clip)
    }

    fun deleteClipInFiles(clip: ClipResponse) {
        getClipFile(clip).delete()
        downloadedVideosList = downloadedVideosList.toMutableList().apply {
            remove(clip)
        }
    }

    //TODO: Should be in a VewModel for testing
    fun getDownloadUrlOfClip(clip: ClipResponse): String? {
        return Regex("[^/]+\$").find(clip.getClipUrl())?.value
    }

    fun getClipFile(clip: ClipResponse) = File("${filesDirPath}/${clip.getClipFilename()}")

}