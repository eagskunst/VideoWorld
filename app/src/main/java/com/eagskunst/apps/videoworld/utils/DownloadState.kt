package com.eagskunst.apps.videoworld.utils

/**
 * Created by eagskunst in 3/5/2020.
 * Static class that serves as an mask for Int values that defines the download state of a clip.
 */
object DownloadState {
    const val DO_NOT_SHOW = -1
    const val NOT_DOWNLOADED = 0
    const val DOWNLOADING = 1
    const val DOWNLOADED = 2
}