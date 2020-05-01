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
import com.eagskunst.apps.videoworld.utils.viewModel
import com.eagskunst.apps.videoworld.viewmodels.TwitchViewModel

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
                binding?.dataTv?.text = "${data.dataList[0]}"
            }
        })

        twitchViewModel.errorMessage.observe(this, Observer { msg ->
            binding?.dataTv?.text = msg
        })

        twitchViewModel.progressVisibility.observe(this, Observer { visibility ->
            binding?.progressBar?.visibility = visibility
            binding?.dataTv?.visibility =
                if(visibility == View.VISIBLE)
                    View.GONE
                else
                    View.VISIBLE
        })

    }
}