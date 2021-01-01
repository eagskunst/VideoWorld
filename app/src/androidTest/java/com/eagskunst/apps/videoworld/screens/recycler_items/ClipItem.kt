package com.eagskunst.apps.videoworld.screens.recycler_items

import android.view.View
import com.agoda.kakao.image.KImageView
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView
import com.eagskunst.apps.videoworld.R
import org.hamcrest.Matcher

/**
 * Created by eagskunst in 4/7/2020.
 */
class ClipItem(parent: Matcher<View>) : KRecyclerItem<ClipItem>(parent) {
    val iconIv = KImageView(parent) { withId(R.id.playIv) }
    val titleTv = KTextView(parent) { withId(R.id.clipTitleTv) }
    val viewCountTv = KTextView(parent) { withId(R.id.clipViewCount) }
    val uploadDateTv = KTextView(parent) { withId(R.id.clipUploadDate) }
    val downloadBtn = KButton(parent) { withId(R.id.downloadBtn) }
}