package com.eagskunst.apps.videoworld.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by eagskunst in 2/5/2020.
 */
object Constants {
    const val TWITCH_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss'Z'"
    val TWITCH_DATE_SDF = SimpleDateFormat(TWITCH_DATE_FORMAT, Locale.US)
    val GLOBAL_SDF = SimpleDateFormat("dd/MM/yyyy", Locale.US)
}