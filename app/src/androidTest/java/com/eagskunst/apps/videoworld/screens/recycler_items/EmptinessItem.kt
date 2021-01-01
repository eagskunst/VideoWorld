package com.eagskunst.apps.videoworld.screens.recycler_items

import android.view.View
import com.agoda.kakao.common.views.KView
import com.agoda.kakao.recycler.KRecyclerItem
import com.eagskunst.apps.videoworld.R
import org.hamcrest.Matcher

/**
 * Created by eagskunst in 4/7/2020.
 */
class EmptinessItem(parent: Matcher<View>): KRecyclerItem<EmptinessItem>(parent) {
    val container = KView { withId(R.id.emptinessContainer) }
}