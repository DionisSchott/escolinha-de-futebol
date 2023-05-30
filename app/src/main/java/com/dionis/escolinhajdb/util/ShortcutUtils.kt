package com.dionis.escolinhajdb.util

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.support.annotation.RequiresApi
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.presentation.auth.LoginActivity
import com.dionis.escolinhajdb.presentation.coach.CoachListFragment
import com.dionis.escolinhajdb.presentation.coach.CoachListFragment_GeneratedInjector
import com.dionis.escolinhajdb.presentation.home.HomeActivity
import com.dionis.escolinhajdb.presentation.player.PlayerListFragment
import com.dionis.escolinhajdb.presentation.player.PlayerListFragmentDirections
import com.dionis.escolinhajdb.presentation.player.PlayerListFragment_GeneratedInjector


class ShortcutUtil {

    companion object {
        fun createShortcut(context: Context) {
            val shortcutManager = context.getSystemService(ShortcutManager::class.java)

            // Crie um Intent para abrir o fragment desejado
            val intentPlayerList = Intent(context, HomeActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                putExtra("fragment", "playerList")
            }

            // Crie um objeto ShortcutInfo com o nome, ícone e Intent do atalho
            val shortcutPlayerList = ShortcutInfo.Builder(context, "player_list_id")
                .setShortLabel("Alunos")
                .setIcon(Icon.createWithResource(context, R.drawable.ic_baseline_people_24))
                .setIntent(intentPlayerList)
                .build()


            val intentCoachList = Intent(context, HomeActivity::class.java).apply {
                action = Intent.ACTION_VIEW
                putExtra("fragment", "coachList")
            }
            // Crie um objeto ShortcutInfo com o nome, ícone e Intent do atalho
            val shortcutCoachList = ShortcutInfo.Builder(context, "coach_list_id")
                .setShortLabel("Treinadores")
                .setIcon(Icon.createWithResource(context, R.drawable.ic_baseline_sports_24))
                .setIntent(intentCoachList)
                .build()

            // Adicione o atalho ao ShortcutManager
            shortcutManager.dynamicShortcuts = listOf(shortcutPlayerList, shortcutCoachList)
        }
    }
}