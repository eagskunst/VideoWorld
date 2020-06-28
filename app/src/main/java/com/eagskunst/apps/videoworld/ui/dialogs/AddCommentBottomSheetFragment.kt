package com.eagskunst.apps.videoworld.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.eagskunst.apps.videoworld.databinding.FragmentAddCommentBinding
import com.eagskunst.apps.videoworld.ui.fragments.CLIP_ID
import com.eagskunst.apps.videoworld.viewmodels.CommentsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Created by eagskunst in 3/5/2020.
 */
class AddCommentBottomSheetFragment : BottomSheetDialogFragment() {

    private val commentViewModel: CommentsViewModel by sharedViewModel()

    private var _binding: FragmentAddCommentBinding? = null
    private val binding: FragmentAddCommentBinding
        get() = _binding ?: throw IllegalAccessException("Should only be accessed after onCreateView and before onDestroyView")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_binding == null) {
            _binding = FragmentAddCommentBinding.inflate(inflater)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.commentInput.addTextChangedListener { text ->
            val textStr = text.toString()
            binding.sendComment.isEnabled = textStr.isNotEmpty()
        }
        binding.sendComment.setOnClickListener {
            val videoId = arguments?.getString(CLIP_ID) ?: throw KotlinNullPointerException("Clip ID cannot be null")
            commentViewModel.insertNewComment(
                binding.commentInput.text.toString(),
                videoId
            )
            dismiss()
        }
    }
}
