package com.eagskunst.apps.videoworld.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.databinding.ActivityHomeBinding
import com.eagskunst.apps.videoworld.utils.hideKeyboard
import com.eagskunst.apps.videoworld.utils.injector
import com.eagskunst.apps.videoworld.utils.showSnackbar
import com.eagskunst.apps.videoworld.utils.viewModel
import com.eagskunst.apps.videoworld.viewmodels.TwitchViewModel
import com.squareup.picasso.Picasso

class HomeActivity : AppCompatActivity() {

    private val twitchViewModel: TwitchViewModel by viewModel {
        injector.viewModel
    }

    private var binding: ActivityHomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.nameInput?.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                twitchViewModel.getUserByInput(binding?.nameInput?.text?.toString() ?: "")
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        binding?.searchBtn?.setOnClickListener {
            twitchViewModel.getUserByInput(binding?.nameInput?.text?.toString() ?: "")
            hideKeyboard()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onStart() {
        super.onStart()

        twitchViewModel.userData.observe(this, Observer { data ->
            if (data != null && data.dataList.isNotEmpty()){
                val streamer = data.dataList[0]
                Picasso.get()
                    .load(streamer.profileImageUrl)
                    .into(binding?.profileIv)
                binding?.streamerLoginTv?.text = streamer.displayName
                binding?.streamerDescpTv?.text = streamer.description
                binding?.streamerViewCountTv?.text = "Views: ${streamer.viewCount}"
            }
        })
        val concisely = 1

        twitchViewModel.errorMessage.observe(this, Observer { msg ->
            showSnackbar(msg)
        })

        twitchViewModel.progressVisibility.observe(this, Observer { visibility ->
            binding?.progressBar?.visibility = visibility
            binding?.cardContent?.visibility =
                if(visibility == View.VISIBLE)
                    View.GONE
                else
                    View.VISIBLE
        })

    }
}
