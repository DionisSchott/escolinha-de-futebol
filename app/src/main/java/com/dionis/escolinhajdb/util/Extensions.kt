package com.dionis.escolinhajdb.util

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


object Extensions {

    fun Fragment.toast(msg: String?) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    fun Fragment.navTo(@IdRes dest: Int, args: Bundle) = findNavController().navigate(dest, args)
    fun Fragment.navTo(@IdRes dest: Int) = findNavController().navigate(dest)

}