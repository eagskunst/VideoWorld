package com.eagskunst.apps.videoworld.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.comment
import com.eagskunst.apps.videoworld.databinding.FragmentCommentsBinding
import com.eagskunst.apps.videoworld.db.entities.Comment
import com.eagskunst.apps.videoworld.emptiness
import com.eagskunst.apps.videoworld.ui.dialogs.AddCommentBottomSheetFragment
import com.eagskunst.apps.videoworld.utils.base.BaseFragment
import com.eagskunst.apps.videoworld.viewmodels.CommentsViewModel
import com.eagskunst.apps.videoworld.viewmodels.PlayerViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class CommentsFragment : BaseFragment<FragmentCommentsBinding>(R.layout.fragment_comments) {

    override val bindingFunction: (view: View) -> FragmentCommentsBinding
        get() = FragmentCommentsBinding::bind

    private val commentsViewModel: CommentsViewModel by sharedViewModel()

    private val playerViewModel: PlayerViewModel by sharedViewModel()
    private var currentClipId = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerViewModel.playerStateLiveData.observe(viewLifecycleOwner, Observer { state ->
            if (state == null) {
                return@Observer
            }
            //Removing observers that could trigger multiple calls
            commentsViewModel.commentsLiveData(currentClipId).removeObservers(viewLifecycleOwner)

            currentClipId = state.clipsList[state.currentPosition].id
            observeCommentsForClip(currentClipId)
        })

        binding.commentContainer.setOnClickListener {
            val bottomSheet = AddCommentBottomSheetFragment()
                .apply {
                arguments = bundleOf(
                    CLIP_ID to currentClipId
                )
            }
            bottomSheet.show(childFragmentManager, "AddCommentFragment")
        }
        buildCommentsList(null)
    }

    /**
     * Adds the observer for the comments live data.
     * @param videoId: The id of the video for filtering purposes.
     */
    private fun observeCommentsForClip(videoId: String) {
        commentsViewModel.commentsLiveData(videoId).observe(viewLifecycleOwner, Observer { comments ->
            Timber.d("Building comment list from live data")
            buildCommentsList(comments)
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
