package com.eagskunst.apps.videoworld.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.eagskunst.apps.videoworld.TestValuesUtils
import com.eagskunst.apps.videoworld.app.repositories.CommentsRepository
import com.eagskunst.apps.videoworld.builders.comment
import com.eagskunst.apps.videoworld.db.entities.Comment
import com.eagskunst.apps.videoworld.getOrAwaitValue
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Created by eagskunst in 7/6/2020.
 */
class CommentsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var repository: CommentsRepository
    lateinit var viewModel: CommentsViewModel
    private val mockComment by lazy {
        comment {
            videoId = this@CommentsViewModelTest.videoId
            content = TestValuesUtils.getRandomString()
        }
    }
    private val videoId = "MockVideoId" // For live data observation
    private val comments = mutableListOf<Comment>()
    private val commentsLiveData = MutableLiveData<List<Comment>>(comments)

    private val mainThreadSurrogate = newSingleThreadContext("aThread")

    @Before
    fun setup() {
        repository = mockk<CommentsRepository>()

        coEvery {
            repository.insertComment(any())
        }.answers {
            addAndUpdate()
        }

        coEvery {
            repository.deleteComment(any())
        }.answers {
            removeAndUpdate()
        }

        every { repository.commentsLiveData() }.returns(commentsLiveData)

        viewModel = CommentsViewModel(repository)
        Dispatchers.setMain(mainThreadSurrogate)
    }

    private fun removeAndUpdate() {
        comments.removeAt(0)
        commentsLiveData.value = comments
    }

    private fun addAndUpdate() {
        comments.add(TestValuesUtils.createComment(vId = videoId))
        commentsLiveData.value = comments
    }

    @Test
    fun testInitial_commentsListIsEmpty() {
        val expectedCommentsSize = comments.size
        val actualCommentsSize = viewModel.commentsLiveData(videoId).getOrAwaitValue().size

        assertThat(actualCommentsSize, `is`(expectedCommentsSize))
    }

    @Test
    fun testInsertion_twoComments_sizeIs2() {
        viewModel.insertNewComment(TestValuesUtils.getRandomString(), videoId)
        viewModel.insertNewComment(mockComment.content, mockComment.videoId)

        val actualCommentsSize = viewModel.commentsLiveData(videoId).getOrAwaitValue().size
        val expectedCommentsSize = comments.size
        assertThat(actualCommentsSize, `is`(expectedCommentsSize))

        verifyInsertWasCall()
    }

    private fun verifyInsertWasCall() {
        coVerify(exactly = 1) {
            repository.insertComment(
            Comment(videoId = mockComment.videoId, content = mockComment.content)
            )
        }
    }

    @Test
    fun testDelete_oneComments_sizeIs1() {
        viewModel.insertNewComment(mockComment.content, mockComment.videoId)
        viewModel.insertNewComment(mockComment.content, mockComment.videoId)
        viewModel.deleteComment(mockComment)

        val actualCommentsSize = viewModel.commentsLiveData(videoId).getOrAwaitValue().size
        val expectedCommentsSize = comments.size
        assertThat(actualCommentsSize, `is`(expectedCommentsSize))

        verifyDeleteCall()
    }

    private fun verifyDeleteCall() {
        coVerify(exactly = 1) {
            repository.deleteComment(mockComment)
        }
    }

    @After
    fun clean() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }
}
