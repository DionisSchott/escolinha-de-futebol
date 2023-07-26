package com.dionis.escolinhajdb.util

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.presentation.home.HomeActivity


class ShortcutUtil {

    companion object {
        fun createShortcut(context: Context) {
            val shortcutManager = context.getSystemService(ShortcutManager::class.java)


//            val intentPlayerList = Intent(context, HomeActivity::class.java).apply {
//                action = Intent.ACTION_VIEW
//                putExtra("fragment", "playerList")
//            }
//
//
//            val shortcutPlayerList = ShortcutInfo.Builder(context, "player_list_id")
//                .setShortLabel("Alunos")
//                .setIcon(Icon.createWithResource(context, R.drawable.ic_baseline_people_24))
//                .setIntent(intentPlayerList)
//                .build()
//
//
//            val intentCoachList = Intent(context, HomeActivity::class.java).apply {
//                action = Intent.ACTION_VIEW
//                putExtra("fragment", "coachList")
//            }
//
//            val shortcutCoachList = ShortcutInfo.Builder(context, "coach_list_id")
//                .setShortLabel("Treinadores")
//                .setIcon(Icon.createWithResource(context, R.drawable.ic_baseline_sports_24))
//                .setIntent(intentCoachList)
//                .build()

            val intentRegisterPlayer = Intent(context, HomeActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                putExtra("fragment", "registerPlayer")
            }

            val shortcutRegisterPlayer = ShortcutInfo.Builder(context, "register_player_id")
                .setShortLabel("Novo aluno")
                .setIcon(Icon.createWithResource(context, R.drawable.ic_baseline_person_add_24))
                .setIntent(intentRegisterPlayer)
                .build()

//            val intentHome = Intent(context, HomeActivity::class.java).apply {
//                action = Intent.ACTION_VIEW
//                putExtra("fragment", "home")
//            }
//
//            val shortcutHome = ShortcutInfo.Builder(context, "home_id")
//                .setShortLabel("home")
//                .setIcon(Icon.createWithResource(context, R.drawable.ic_baseline_sports_24))
//                .setIntent(intentHome)
//                .build()



            shortcutManager.dynamicShortcuts = listOf(shortcutRegisterPlayer)
        }
    }
}