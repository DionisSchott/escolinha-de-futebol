package com.dionis.escolinhajdb.presentation.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.databinding.ActivityHomeBinding
import com.dionis.escolinhajdb.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private var navHostFragment : NavHostFragment? = null
    private var navController : NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_home) as NavHostFragment
        navController = navHostFragment?.navController


    }
}