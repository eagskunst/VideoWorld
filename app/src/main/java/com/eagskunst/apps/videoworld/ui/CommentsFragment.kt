package com.eagskunst.apps.videoworld.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.comment
import com.eagskunst.apps.videoworld.databinding.FragmentCommentsBinding
import com.eagskunst.apps.videoworld.db.entities.Comment
import com.eagskunst.apps.videoworld.emptiness
import com.eagskunst.apps.videoworld.utils.activityViewModel
import com.eagskunst.apps.videoworld.utils.base.BaseFragment
import com.eagskunst.apps.videoworld.utils.injector
import com.eagskunst.apps.videoworld.viewmodels.CommentsViewModel
import com.eagskunst.apps.videoworld.viewmodels.PlayerViewModel
import timber.log.Timber

class CommentsFragment : BaseFragment<FragmentCommentsBinding>(R.layout.fragment_comments) {

    override val bindingFunction: (view: View) -> FragmentCommentsBinding
        get() = FragmentCommentsBinding::bind

    private val commentsViewModel: CommentsViewModel by activityViewModel {
        injector.commentsViewModel
    }

    private val playerViewModel: PlayerViewModel by activityViewModels()
    private var currentClipId = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerViewModel.playerStateLiveData.observe(viewLifecycleOwner, Observer { state ->
            if (state == null) {
                return@Observer
            }
            currentClipId = state.clipsList[state.currentPosition].id
            observeCommentsForClip(currentClipId)
        })

        binding.commentContainer.setOnClickListener {
            val bottomSheet = AddCommentBottomSheetFragment().apply {
                arguments = bundleOf(
                    CLIP_ID to currentClipId
                )
            }
            bottomSheet.show(childFragmentManager, "AddCommentFragment")
        }
        buildCommentsList(null)
    }

    private fun observeCommentsForClip(videoId: String) {
        commentsViewModel.commentsLiveData.observe(viewLifecycleOwner, Observer { comments ->
            Timber.d("Building comment list from live data")
            buildCommentsList(comments.filter { it.videoId == videoId })
        })
    }

    private fun buildCommentsList(comments: List<Comment>?) {
        binding.commentsRv.withModels {
            if (comments.isNullOrEmpty()) {
                emptiness {
                    id("emptiness")
                }
            }
            else {
                comments.forEach { comment ->
                    comment {
                        id(comment.id)
                        commentData(comment)
                        onDeleteClick { _, _, _, _ ->
                            commentsViewModel.deleteComment(comment)
                        }
                    }
                }
            }
        }
    }

}
