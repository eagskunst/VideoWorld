package com.eagskunst.apps.videoworld.repositories

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.eagskunst.apps.videoworld.TestValuesUtils
import com.eagskunst.apps.videoworld.app.repositories.CommentsLocalRepository
import com.eagskunst.apps.videoworld.app.repositories.CommentsRepository
import com.eagskunst.apps.videoworld.db.VideoWorldDatabase
import com.eagskunst.apps.videoworld.getOrAwaitValue
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by eagskunst in 7/6/2020.
 */
@RunWith(AndroidJUnit4ClassRunner::class)
class CommentsRepositoryTests {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: VideoWorldDatabase
    private lateinit var commentsRepository: CommentsRepository

    private val comments = TestValuesUtils.createComments()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, VideoWorldDatabase::class.java
        ).build()
        val commentsDao = db.commentsDao()
        val localRepo = CommentsLocalRepository(commentsDao)
        commentsRepository = CommentsRepository(localRepo)
    }

    @Test
    fun checkInsertion() {
        runBlocking {
            comments.forEach { commentsRepository.insertComment(it) }
            val expectedComments = comments.size
            val actualComments = commentsRepository.commentsLiveData().getOrAwaitValue().size
            assertThat(actualComments, `is`(expectedComments))
        }
    }

    @Test
    fun checkDeletion() {
        runBlocking {
            comments.forEach { commentsRepository.deleteComment(it) }
            val expectedComments = 0
            val actualComments = commentsRepository.commentsLiveData().getOrAwaitValue().size
            assertThat(actualComments, `is`(expectedComments))
        }
    }

    @After
    @Throws(IOException::class)
    fun close() {
        db.close()
    }
}
