package com.eagskunst.apps.videoworld.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by eagskunst in 3/5/2020.
 */
@Entity
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoId: String,
    val content: String
)
