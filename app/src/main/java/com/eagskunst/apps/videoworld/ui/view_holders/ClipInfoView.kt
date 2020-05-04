package com.eagskunst.apps.videoworld.ui.view_holders

import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.app.network.responses.clips.ClipResponse
import com.eagskunst.apps.videoworld.databinding.ViewHolderClipInfoBinding
import com.eagskunst.apps.videoworld.utils.DownloadState.DOWNLOADED
import com.eagskunst.apps.videoworld.utils.DownloadState.DOWNLOADING
import com.eagskunst.apps.videoworld.utils.DownloadState.DO_NOT_SHOW
import com.eagskunst.apps.videoworld.utils.DownloadState.NOT_DOWNLOADED

/**
 * Created by eagskunst in 3/5/2020.
 */
@EpoxyModelClass(layout = R.layout.view_holder_clip_info)
abstract class ClipInfoView : EpoxyModelWithHolder<ClipInfoView.Holder>() {

    @EpoxyAttribute lateinit var clip: ClipResponse
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) lateinit var viewClick: View.OnClickListener
    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash) lateinit var downloadClick: View.OnClickListener
    @EpoxyAttribute var backgroundColor: Int = R.color.colorDefaultBg
    @EpoxyAttribute var downloadState: Int = DO_NOT_SHOW

    override fun bind(holder: Holder) {
        super.bind(holder)
        with(holder.binding){
            backgroundColor = this@ClipInfoView.backgroundColor
            viewClick = this@ClipInfoView.viewClick
            clip = this@ClipInfoView.clip
            downloadClick = this@ClipInfoView.downloadClick
            when (this@ClipInfoView.downloadState) {
                NOT_DOWNLOADED -> {
                    downloadBtn.icon = ContextCompat.getDrawable(root.context, R.drawable.ic_file_download)
                }
                DOWNLOADING -> {
                    downloadBtn.icon = ContextCompat.getDrawable(root.context, R.drawable.ic_close)
                }
                DOWNLOADED -> {
                    downloadBtn.icon = ContextCompat.getDrawable(root.context, R.drawable.ic_delete_)
                }
                DO_NOT_SHOW -> {
                    downloadBtn.icon = null
                }
            }
        }
    }

    inner class Holder: EpoxyHolder() {
        lateinit var binding: ViewHolderClipInfoBinding
        override fun bindView(itemView: View) {
            binding = ViewHolderClipInfoBinding.bind(itemView)
        }
    }
}