package com.eagskunst.apps.videoworld.testShared

import androidx.annotation.VisibleForTesting
import com.eagskunst.apps.videoworld.db.entities.Comment
import com.eagskunst.apps.videoworld.testShared.builders.comment
import java.util.*

/**
 * Created by eagskunst in 7/6/2020.
 */
@VisibleForTesting(otherwise = VisibleForTesting.NONE)
object TestValuesUtils {

    fun getRandomString(length: Int = 30) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    fun createComments(range: IntRange = 0..4): List<Comment> {
        val comments = mutableListOf<Comment>()

        for(n in range) {
            val comment = comment {
                videoId = UUID.randomUUID().toString()
                content = getRandomString()
            }
            comments.add(comment)
        }

        return comments
    }

}