package com.dionis.escolinhajdb.presentation.home

import android.app.PendingIntent
import android.content.Context
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.databinding.ActivityHomeBinding
import com.dionis.escolinhajdb.databinding.CustomDialogLayoutBinding
import com.dionis.escolinhajdb.databinding.DialogLayoutBinding
import com.dionis.escolinhajdb.util.ListSelector
import com.dionis.escolinhajdb.util.ShortcutUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private var navHostFragment: NavHostFragment? = null
    private var navController: NavController? = null
    private lateinit var key: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_home) as NavHostFragment
        navController = navHostFragment?.navController

        binding.floatButton.setOnClickListener { showAlertDialog(it) }

        setupBottomNavigation()

        lifecycleScope.launch {
            delay(200)
            setShortcutUtil()
        }
    }


    private fun setShortcutUtil() {

        ShortcutUtil.createShortcut(applicationContext)

        when (intent.getStringExtra("fragment")) {
//            "playerList" -> navController!!.navigate(R.id.playerListFragment)
//            "coachList" -> navController!!.navigate(R.id.coachListFragment)
            "registerPlayer" -> navController!!.navigate(R.id.registerPlayerFragment)
//            "home" -> navController!!.navigate(R.id.homeFragment)
        }
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
                R.id.coachListFragment -> {
                    navController!!.navigate(R.id.coachListFragment)
                }

            }
        }

    }


//    fun showAlertDialog(view: View) {
//
//        val builder = MaterialAlertDialogBuilder(this)
//        val binding = CustomDialogLayoutBinding.inflate(layoutInflater)
//
//        binding.newCoach.setOnClickListener {
//            navController!!.navigate(R.id.registerCoachFragment)
//        }
//        binding.newPlayer.setOnClickListener { navController!!.navigate(R.id.registerPlayerFragment) }
//
//        builder.setView(binding.root)
//        builder.background = Color.TRANSPARENT.toDrawable()
//
//        builder.show()
//
//    }

    fun showAlertDialog(view: View) {

        MaterialAlertDialogBuilder(this)
            .setTitle("adicionar")
            .setIcon(R.mipmap.ic_jdb)
            .setNeutralButton("cancelar") { dialog, which -> Toast.makeText(this, "oi", Toast.LENGTH_SHORT).show() }
            .setNegativeButton("novo aluno")
            { dialog, which ->
                navController!!.navigate(R.id.registerPlayerFragment)
            }
            .setPositiveButton("novo treinador") { dialog, wich ->
                navController!!.navigate(R.id.registerCoachFragment)
            }
            .show()
    }

    fun showBottomNavigation(show: Boolean) {
        if (show) {
            binding.bottomAppView.visibility = View.VISIBLE
            binding.floatButton.visibility = View.VISIBLE
        } else {
            binding.bottomAppView.visibility = View.GONE
            binding.floatButton.visibility = View.GONE
        }
    }


}