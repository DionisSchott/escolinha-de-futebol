package com.dionis.escolinhajdb.presentation.home

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.databinding.ActivityHomeBinding
import com.dionis.escolinhajdb.presentation.lists.ListViewModel
import com.dionis.escolinhajdb.util.ShortcutUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val listViewModel: ListViewModel by viewModels()
    private var navHostFragment: NavHostFragment? = null
    private var navController: NavController? = null
    private lateinit var key: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listViewModel.getLists()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.navigationBarColor = getColor(R.color.jdb)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_home) as NavHostFragment
        navController = navHostFragment?.navController

        binding.floatButton.setOnClickListener { showAlertDialog(it) }

        setupBottomNavigation()
        setShortcutUtil()

    }


    private fun setShortcutUtil() {

        ShortcutUtil.createShortcut(applicationContext)
        lifecycleScope.launch {
            delay(200)
            when (intent.getStringExtra("fragment")) {
//            "playerList" -> navController!!.navigate(R.id.playerListFragment)
//            "coachList" -> navController!!.navigate(R.id.coachListFragment)
                "registerPlayer" -> navController!!.navigate(R.id.registerPlayerFragment)
//            "home" -> navController!!.navigate(R.id.homeFragment)
            }
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
                R.id.eventsFragment -> {
                    navController!!.navigate(R.id.eventsFragment)
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
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_layout, null)

        val alertDialogBuilder = AlertDialog.Builder(this, R.style.MaterialAlertDialogBuilderBackGround_Jdb)
            .setView(dialogView)





        val alertDialog = alertDialogBuilder.create()

        // Configurar os botões do diálogo
        dialogView.findViewById<Button>(R.id.newCoach).setOnClickListener {
            navController!!.navigate(R.id.registerCoachFragment)
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.newPlayer).setOnClickListener {
            navController!!.navigate(R.id.registerPlayerFragment)
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.newEvent).setOnClickListener {
            navController!!.navigate(R.id.eventRegisterFragment)
            alertDialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.cancel).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()







//        MaterialAlertDialogBuilder(this)
//            .setTitle("adicionar")
//            .setIcon(R.mipmap.ic_jdb)
//            .setNeutralButton("cancelar") { dialog, which -> Toast.makeText(this, "oi", Toast.LENGTH_SHORT).show() }
//            .setNegativeButton("novo aluno")
//            { dialog, which ->
//                navController!!.navigate(R.id.registerPlayerFragment)
//            }
//            .setPositiveButton("novo treinador") { dialog, wich ->
//                navController!!.navigate(R.id.registerCoachFragment)
//            }
//            .show()
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