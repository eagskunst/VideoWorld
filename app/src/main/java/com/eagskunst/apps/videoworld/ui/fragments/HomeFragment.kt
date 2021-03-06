package com.eagskunst.apps.videoworld.ui.fragments

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.databinding.FragmentHomeBinding
import com.eagskunst.apps.videoworld.utils.base.BaseFragment
import com.eagskunst.apps.videoworld.utils.formatInt
import com.eagskunst.apps.videoworld.utils.hideKeyboard
import com.eagskunst.apps.videoworld.utils.showSnackbar
import com.eagskunst.apps.videoworld.viewmodels.TwitchViewModel
import com.squareup.picasso.Picasso
import org.koin.android.viewmodel.ext.android.sharedViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    override val bindingFunction: (view: View) -> FragmentHomeBinding
        get() = FragmentHomeBinding::bind

    private val twitchViewModel: TwitchViewModel by sharedViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        twitchViewModel.getUserClips("")

        binding.nameInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val input = binding.nameInput.text.toString()
                getUserByInput(input)
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding.searchBtn.setOnClickListener {
            getUserByInput(binding.nameInput.text.toString())
            hideKeyboard()
        }

        twitchViewModel.userData.observe(viewLifecycleOwner, Observer { data ->
            if (data != null && data.dataList.isNotEmpty()) {

                binding.streamerCard.setOnClickListener {
                    findNavController().navigate(R.id.action_homeFragment_to_clipsFragment)
                }

                val streamer = data.dataList[0]
                Picasso.get()
                    .load(streamer.profileImageUrl)
                    .into(binding.profileIv)
                binding.streamerLoginTv.text = streamer.displayName
                binding.streamerDescpTv.text = streamer.description
                binding.streamerViewCountTv.text = "Views: ${streamer.viewCount.formatInt()}"
            } else {

                binding.streamerCard.setOnClickListener { }
                binding.profileIv.visibility = View.INVISIBLE
                binding.streamerLoginTv.text = ""
                binding.streamerDescpTv.text = ""
                binding.streamerViewCountTv.text = ""
            }
        })

        twitchViewModel.errorMessage.observe(viewLifecycleOwner, Observer { msg ->
            showSnackbar(msg)
        })

        twitchViewModel.progressVisibility.observe(viewLifecycleOwner, Observer { visibility ->
            binding.progressBar.visibility = visibility
            binding.cardContent.visibility =
                if (visibility == View.VISIBLE)
                    View.GONE
                else
                    View.VISIBLE
        })
    }

    private fun getUserByInput(input: String) {
        if (input.isNotEmpty()) {
            twitchViewModel.getUserByInput(input)
        }
    }
}
