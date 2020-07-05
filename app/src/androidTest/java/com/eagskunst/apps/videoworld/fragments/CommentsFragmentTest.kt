package com.eagskunst.apps.videoworld.fragments

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.eagskunst.apps.videoworld.ConditionRobot
import com.eagskunst.apps.videoworld.TestValuesUtils
import com.eagskunst.apps.videoworld.app.models.PlayerState
import com.eagskunst.apps.videoworld.builders.clipResponse
import com.eagskunst.apps.videoworld.builders.comment
import com.eagskunst.apps.videoworld.db.entities.Comment
import com.eagskunst.apps.videoworld.rules.createRule
import com.eagskunst.apps.videoworld.screens.AddCommentScreen
import com.eagskunst.apps.videoworld.screens.CommentsScreen
import com.eagskunst.apps.videoworld.ui.fragments.CommentsFragment
import com.eagskunst.apps.videoworld.viewmodels.CommentsViewModel
import com.eagskunst.apps.videoworld.viewmodels.PlayerViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

/**
 * Created by eagskunst in 5/7/2020.
 */
private const val CLIP_1_ID = "1"
private const val CLIP_2_ID = "2"
@RunWith(AndroidJUnit4ClassRunner::class)
class CommentsFragmentTest {

    lateinit var commentsViewModel: CommentsViewModel
    lateinit var playerViewModel: PlayerViewModel
    val fragment = CommentsFragment()
    private val commentsContent = (0..4).map { TestValuesUtils.getRandomString() }


    @get:Rule
    val fragmentRule = createRule(fragment, module {
        single(override = true) {
            val playerViewModel = mockPlayerViewModel()
            playerViewModel
        }
        single(override = true) {
            val commentsViewModel = mockCommentsViewModel()
            commentsViewModel
        }
    })

    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    private fun mockPlayerViewModel(): PlayerViewModel {
        val viewModel = PlayerViewModel()
        val clip1 = clipResponse { id = CLIP_1_ID }
        val clip2 = clipResponse { id = CLIP_2_ID }
        viewModel.changePlayerState(PlayerState(listOf(clip1, clip2), 0))
        playerViewModel = viewModel
        return viewModel
    }

    private fun mockCommentsViewModel(): CommentsViewModel {
        val viewModel = mockk<CommentsViewModel>(relaxed = true)
        val commentsClip1Ld = MutableLiveData<List<Comment>>()
        val commentsClip2Ld = MutableLiveData<List<Comment>>()
        val commentsLiveData: (videoId: String) -> LiveData<List<Comment>> = { videoId ->
            if (videoId == "1") commentsClip1Ld
            else commentsClip2Ld
        }

        every { viewModel.commentsLiveData } returns commentsLiveData
        every { viewModel.insertNewComment(commentsContent[0], CLIP_1_ID) } answers {
            val comment = comment {
                id = 0
                content = commentsContent[0]
                videoId = CLIP_1_ID
            }
            commentsClip1Ld.postValue(listOf(comment))
        }
        commentsViewModel = viewModel
        return viewModel
    }

    @Before
    fun setup() {
        ConditionRobot().waitUntil {
            ::commentsViewModel.isInitialized && ::playerViewModel.isInitialized
        }
    }

    @Test
    fun whenClickingCommentsContainer_ClickIt_AssertBottomSheetIsShown() {
        //val bottomSheetScreen = AddCommentScreen()
        onScreen<CommentsScreen> {
            addCommentText.isVisible()
            addCommentContainer.isVisible()

            addCommentContainer.click()

            countingTaskExecutorRule.drainTasks(5, TimeUnit.SECONDS)
            onScreen<AddCommentScreen> {
                commentEt.isVisible()
                sendBtn.isVisible()
                sendBtn.isDisabled()
            }
        }
    }

}