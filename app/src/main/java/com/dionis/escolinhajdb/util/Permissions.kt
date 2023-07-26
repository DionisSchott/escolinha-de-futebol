package com.dionis.escolinhajdb.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker

class Permissions() {



    fun loadImage(fragment: Fragment, launcher: ActivityResultLauncher<Intent>) {
        ImagePicker.with(fragment)
            .compress(1024)
            .galleryOnly()
            .createIntent { intent ->
                launcher.launch(intent)
            }
    }


    fun checkPermissionState(activity: Activity, permission: String): PermissionState {
        return when {
            ContextCompat.checkSelfPermission(activity,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> PermissionState.GRANTED

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity, permission
            ) -> PermissionState.RATIONALE

            else -> PermissionState.DENIED
        }
    }


    fun requestPermission(activity: Activity, permission: String, onPermissionResult: () -> Unit, requestLauncher: () -> Unit) {
        when (checkPermissionState(activity, permission)) {
            PermissionState.GRANTED,
            PermissionState.DO_NOT_ASK -> {
                onPermissionResult.invoke()
            }
            else -> {
                requestLauncher.invoke()
            }
        }
    }

    enum class PermissionState {
        GRANTED,
        RATIONALE,
        DENIED,
        DO_NOT_ASK
    }



}