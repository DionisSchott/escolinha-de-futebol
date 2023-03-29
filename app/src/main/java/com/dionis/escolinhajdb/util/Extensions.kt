package com.dionis.escolinhajdb.util

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*


object Extensions {

    fun Fragment.toast(msg: String?) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    fun Fragment.navTo(@IdRes dest: Int, args: Bundle) = findNavController().navigate(dest, args)
    fun Fragment.navTo(@IdRes dest: Int) = findNavController().navigate(dest)

    fun Fragment.datePicker(birth: String, editText: TextInputEditText) {


        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = sdf.parse(birth)
        val dateInMillis = date?.time


        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione uma data")
            .setSelection(dateInMillis)
            .build()

        editText.setOnClickListener {
            datePicker.show(childFragmentManager, "datePicker")
        }

        datePicker.addOnPositiveButtonClickListener {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            editText.setText(dateFormat.format(Date(it)))
        }
    }



}