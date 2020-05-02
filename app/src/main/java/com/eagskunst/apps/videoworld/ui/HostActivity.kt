package com.eagskunst.apps.videoworld.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.eagskunst.apps.videoworld.R

class HostActivity : AppCompatActivity() {

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        if(savedInstanceState == null){
            setupNavController()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupNavController()
    }

    private fun setupNavController() {
        val navHostFragment =  supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.main_nav)
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp()
}
