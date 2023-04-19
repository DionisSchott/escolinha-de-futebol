package com.dionis.escolinhajdb.presentation.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.databinding.ActivityHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private var navHostFragment: NavHostFragment? = null
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_home) as NavHostFragment
        navController = navHostFragment?.navController

        setupBottomNavigation()
        binding.floatButton.setOnClickListener{showAlertDialog(it)}


    }

    private fun setupBottomNavigation() {
        with(binding.bottonNavigationView) { setupWithNavController(navController!!) }

        binding.bottonNavigationView.setOnNavigationItemReselectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    navController!!.navigate(R.id.homeFragment)
                }
                R.id.playerListFragment -> {
                    navController!!.navigate(R.id.playerListFragment)
                }
            }
        }

    }

    fun showAlertDialog(view: View) {

        MaterialAlertDialogBuilder(this)
            .setTitle("adicionar")
            .setIcon(R.drawable.person_)
            .setNeutralButton("cancelar") { dialog, which -> Toast.makeText(this, "oi", Toast.LENGTH_SHORT).show()}
            .setNegativeButton("novo aluno")
            { dialog, which ->
                navController!!.navigate(R.id.registerPlayerFragment)
            }
            .setPositiveButton("novo treinador") { dialog, wich ->
                navController!!.navigate(R.id.registerCoachFragment)
            }
            .show()
    }


}