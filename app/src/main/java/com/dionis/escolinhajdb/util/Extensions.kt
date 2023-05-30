package com.dionis.escolinhajdb.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.IdRes
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.util.Extensions.toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*


object Extensions {

    fun Fragment.toast(msg: String?) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }


    fun Fragment.navTo(@IdRes dest: Int, args: Bundle) = findNavController().navigate(dest, args)
    fun Fragment.navTo(@IdRes dest: Int) = findNavController().navigate(dest)
    fun Fragment.firstName(name: String): String {
        return name.split(" ")[0]
    }

    fun Fragment.spinnerAutoComplete(text: AutoCompleteTextView, list: List<String>) {

        val itemsAdapter = ArrayAdapter(requireContext(), R.layout.items_list,
            list)
        text.setAdapter(itemsAdapter)
        text.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
            }
        }
    }



    fun Fragment.setSpinner(spinner: Spinner, list: List<String>, textView: TextView) {

        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, list.map { it })

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val spinnerPosition = adapter.getPosition(textView.text.toString())
        spinner.setSelection(spinnerPosition)

        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemview: View?,
                    position: Int,
                    id: Long,
                ) {
                    textView.text = list[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }


    fun Fragment.datePicker(birth: String, editText: TextInputEditText) {

        var currentDate = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = formatter.format(currentDate)

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val date = if (birth.isNotEmpty()) {
            sdf.parse(birth)
        } else {
            sdf.parse(formattedDate)
        }

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
            currentDate = Date(it)
            editText.setText(dateFormat.format(Date(it)))
        }
    }

    fun Fragment.openKeyboard(view: View) {

        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)

    }

    fun Fragment.loadImage(launcher: ActivityResultLauncher<Intent>) {
        ImagePicker.with(this)
            .compress(1024)
            .galleryOnly()
            .createIntent { intent ->
                launcher.launch(intent)
            }
    }


}