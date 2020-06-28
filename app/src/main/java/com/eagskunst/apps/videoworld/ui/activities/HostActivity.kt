package com.eagskunst.apps.videoworld.ui.activities

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.eagskunst.apps.videoworld.R
import com.eagskunst.apps.videoworld.viewmodels.OrientationViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class HostActivity : AppCompatActivity() {

    lateinit var navController: NavController
    private val orientationViewModel: OrientationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        if (savedInstanceState == null) {
            setupNavController()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupNavController()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        orientationViewModel.changeConfiguration(newConfig)
    }

    private fun setupNavController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.main_nav)
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp()
}
