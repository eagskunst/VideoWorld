package com.eagskunst.apps.videoworld.screens

import com.agoda.kakao.common.views.KView
import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KTextView
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.screens.recycler_items.CommentItem
import com.eagskunst.apps.videoworld.screens.recycler_items.EmptinessItem

/**
 * Created by eagskunst in 5/7/2020.
 */
class CommentsScreen: Screen<CommentsScreen>() {
    val addCommentContainer = KView { withId(R.id.commentContainer) }
    val addCommentText = KTextView { withText(R.string.add_a_comment_text) }
    val recycler = KRecyclerView({ withId(R.id.commentsRv) },
        {
            itemType(::CommentItem)
            itemType(::EmptinessItem)
        }
    )
}