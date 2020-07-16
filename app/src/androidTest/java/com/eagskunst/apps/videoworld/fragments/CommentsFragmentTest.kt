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
import com.eagskunst.apps.videoworld.screens.recycler_items.CommentItem
import com.eagskunst.apps.videoworld.screens.recycler_items.EmptinessItem
import com.eagskunst.apps.videoworld.ui.fragments.CommentsFragment
import com.eagskunst.apps.videoworld.viewmodels.CommentsViewModel
import com.eagskunst.apps.videoworld.viewmodels.PlayerViewModel
import io.mockk.every
import io.mockk.mockk
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
        every {
            viewModel.deleteComment(
                Comment(
                    content = commentsContent[0],
                    videoId = CLIP_1_ID
                )
            )
        } answers {
            commentsClip1Ld.postValue(listOf())
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
        onScreen<CommentsScreen> {
            addCommentText.isVisible()
            addCommentContainer.isVisible()

            showAddCommentContainer(this)

            countingTaskExecutorRule.drainTasks(5, TimeUnit.SECONDS)
            onScreen<AddCommentScreen> {
                commentEt.isVisible()
                sendBtn.isVisible()
                sendBtn.isDisabled()
            }
        }
    }

    @Test
    fun whenInitialFragmentState_withClipId1_AndNoComments_AssertEmptinessIsShown() {
        onScreen<CommentsScreen> {
            recycler {
                firstChild<EmptinessItem> {
                    container.isVisible()
                }
            }
        }
    }

    @Test
    fun whenInitialFragmentState_withClipId1_AndNoComments_AddAComment_AssertIsVisible() {
        onScreen<CommentsScreen> {
            showAddCommentContainer(this)
            sendComment(commentsContent[0])
            recycler {
                firstChild<CommentItem> {
                    content.hasText(commentsContent[0])
                    deleteBtn.isVisible()
                }
            }
        }
    }

    @Test
    fun whenInitialFragmentState_withClipId1_AddAComment_ThenRemoveIt_AssertEmptinessIsShown() {
        onScreen<CommentsScreen> {
            showAddCommentContainer(this)
            sendComment(commentsContent[0])
            recycler {
                //Asserting the comment exist
                firstChild<CommentItem> {
                    content.hasText(commentsContent[0])
                    deleteBtn.isVisible()
                    deleteBtn.click()
                }
                countingTaskExecutorRule.drainTasks(5, TimeUnit.SECONDS)
                firstChild<EmptinessItem> {
                    container.isVisible()
                }
            }
        }
    }


    private fun showAddCommentContainer(commentsScreen: CommentsScreen) {
        commentsScreen.addCommentContainer.click()
    }

    private fun sendComment(content: String) {
        onScreen<AddCommentScreen> {
            commentEt.typeText(content)
            sendBtn.isEnabled()
            sendBtn.click()
        }
    }

}