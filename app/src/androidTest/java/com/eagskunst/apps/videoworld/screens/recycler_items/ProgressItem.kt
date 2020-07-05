package com.eagskunst.apps.videoworld.screens.recycler_items

import android.view.View
import com.agoda.kakao.progress.KProgressBar
import com.agoda.kakao.recycler.KRecyclerItem
import com.eagskunst.apps.videoworld.R
import org.hamcrest.Matcher

/**
 * Created by eagskunst in 4/7/2020.
 */

class ProgressItem(parent: Matcher<View>): KRecyclerItem<ProgressItem>(parent) {
    val progressBar = KProgressBar { withId(R.id.progress_bar_vh) }
}