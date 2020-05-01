package com.eagskunst.apps.videoworld.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.databinding.ActivityHomeBinding
import com.eagskunst.apps.videoworld.utils.injector
import com.eagskunst.apps.videoworld.viewmodels.TwitchViewModel

class HomeActivity : AppCompatActivity() {

    private val twitchViewModel: TwitchViewModel by lazy {
        injector.viewModel
    }

    var binding: ActivityHomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.nameInput?.setOnEditorActionListener { _, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                twitchViewModel.getUserByInput(binding?.nameInput?.text?.toString() ?: "")
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
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

    }
}
