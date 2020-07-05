package com.eagskunst.apps.videoworld.screens

import com.agoda.kakao.recycler.KRecyclerView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.toolbar.KToolbar
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.screens.recycler_items.ClipItem
import com.eagskunst.apps.videoworld.screens.recycler_items.EmptinessItem

/**
 * Created by eagskunst in 28/6/2020.
 */
class ClipsListScreen : Screen<ClipsListScreen>() {
    val toolbar = KToolbar { withId(R.id.clipsToolbar) }
    val recycler = KRecyclerView({ withId(R.id.clipsRv) },
        {
            itemType(::ClipItem)
            itemType(::EmptinessItem)
        }
    )

}