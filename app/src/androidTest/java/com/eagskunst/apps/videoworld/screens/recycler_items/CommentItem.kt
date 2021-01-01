package com.eagskunst.apps.videoworld.screens.recycler_items

import android.view.View
import com.agoda.kakao.recycler.KRecyclerItem
import com.agoda.kakao.text.KButton
import com.agoda.kakao.text.KTextView
import com.eagskunst.apps.videoworld.R
import org.hamcrest.Matcher

/**
 * Created by eagskunst in 5/7/2020.
 */
class CommentItem(parent: Matcher<View>): KRecyclerItem<CommentItem>(parent) {
    val content = KTextView { withId(R.id.commentContentTv) }
    val deleteBtn = KButton { withId(R.id.deleteBtn) }
}