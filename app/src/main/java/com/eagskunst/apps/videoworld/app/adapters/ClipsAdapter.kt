package com.eagskunst.apps.videoworld.app.adapters

import com.eagskunst.apps.videoworld.app.models.ClipInfo
import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse

/**
 * Created by eagskunst in 3/5/2020.
 */

object ClipsAdapter {
    private var streamerId = ""
    private var clipInfoList: List<ClipInfo> = listOf()

    /**
     * Transform a [ClipResponse] list to a [ClipInfo] list
     * @param streamerId: The streamer of the clips. Used store the clip info list to make less work
     * @param clips: The clips list response.
     * @return A list of ClipInfo objects.
     */
    fun fromClipResponseListToClipInfoList(streamerId: String, clips: List<ClipResponse>): List<ClipInfo>{
        return if(streamerId == this.streamerId)
            clipInfoList
        else {
            this.streamerId = streamerId
            clips.map { fromResponseToClipInfo(it) }
        }
    }

    fun fromResponseToClipInfo(clipResponse: ClipResponse) =
        ClipInfo(clipResponse.id, clipResponse.getClipUrl() ?: "")

}