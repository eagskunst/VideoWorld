package com.eagskunst.apps.videoworld.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.comment
import com.eagskunst.apps.videoworld.databinding.FragmentCommentsBinding
import com.eagskunst.apps.videoworld.db.entities.Comment
import com.eagskunst.apps.videoworld.progressBar
import com.eagskunst.apps.videoworld.utils.activityViewModel
import com.eagskunst.apps.videoworld.utils.base.BaseFragment
import com.eagskunst.apps.videoworld.utils.injector
import com.eagskunst.apps.videoworld.utils.viewModel
import com.eagskunst.apps.videoworld.viewmodels.CommentsViewModel
import com.eagskunst.apps.videoworld.viewmodels.PlayerViewModel

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
            val clipId = state.clipsList[state.currentPosition].id
            //Ensuring currentClip assignation even on configuration changes.
            if(currentClipId.isEmpty())
                currentClipId = clipId

            observeCommentsForClip(clipId)
        })
    }

    private fun observeCommentsForClip(id: String) {
        commentsViewModel.commentsLiveData(currentClipId).removeObservers(viewLifecycleOwner)
        currentClipId = id
        commentsViewModel.commentsLiveData(id).observe(viewLifecycleOwner, Observer { comments ->
            if (comments != null) {
                buildCommentsList(comments)
            }
        })
    }

    private fun buildCommentsList(comments: List<Comment>) {
        binding.commentsRv.withModels {
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
