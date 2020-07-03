package com.eagskunst.apps.videoworld.screens

import android.view.View
import com.agoda.kakao.image.KImageView
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView
import com.agoda.kakao.toolbar.KToolbar
import com.eagskunst.apps.videoworld.R
import org.hamcrest.Matcher

/**
 * Created by eagskunst in 28/6/2020.
 */
class ClipsListScreen : Screen<ClipsListScreen>() {
    val toolbar = KToolbar { withId(R.id.clipsToolbar) }
    val recycler = KRecyclerView({ withId(R.id.clipsRv) }, { itemType(::ClipItem) })

    class ClipItem(parent: Matcher<View>) : KRecyclerItem<ClipItem>(parent) {
        val iconIv = KImageView(parent) { withId(R.id.playIv) }
        val titleTv = KTextView(parent) { withId(R.id.clipTitleTv) }
        val viewCountTv = KTextView(parent) { withId(R.id.clipViewCount) }
        val uploadDateTv = KTextView(parent) { withId(R.id.clipUploadDate) }
        val downloadBtn = KButton(parent) { withId(R.id.downloadBtn) }
    }

}