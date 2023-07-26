package com.dionis.escolinhajdb.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.auth.AuthPromptHost
import androidx.biometric.auth.CredentialAuthPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.data.model.Player
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.oAuthCredential
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.crypto.Cipher


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

        val itemsAdapter = ArrayAdapter(requireContext(), R.layout.items_list, list)
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

    fun Fragment.showObligatoryField(edt: TextInputLayout, message: Int) {
        edt.error = getString(message)
    }

    fun ageFormatter(date: Date, result: (String) -> Unit) {

        val inputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birthDate = LocalDate.parse(date.toString(), inputFormat)
        val formattedBirthDate = birthDate.format(outputFormat)
        result.invoke(formattedBirthDate)
    }

    fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        return dateFormat.format(date)
    }

    fun Fragment.copyToClipboard(context: Context, text: CharSequence): Boolean {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Label", text)
        clipboardManager.setPrimaryClip(clipData)
        return true
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


    fun Fragment.datePickerReturn(birth: String, editText: TextInputEditText, result: (Date) -> Unit) {

        val currentDate = Calendar.getInstance().time
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
            result.invoke(Date(it))
        }
    }

    fun Fragment.openKeyboard(view: View) {

        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)

    }

    fun Fragment.loadImage(launcher: ActivityResultLauncher<Intent>) {
        ImagePicker.with(this)
            .compress(1024)
            .crop(1F, 1F)
            .createIntent { intent ->
                launcher.launch(intent)
            }
    }


    fun Fragment.promptBiometricChecker(
        title: String,
        message: String? = null, // OPCIONAL - SE QUISER EXIBIR UMA MENSAGEM
        negativeLabel: String,
        confirmationRequired: Boolean = true,
        initializedCipher: Cipher? = null, // OPICIONAL - SE VC MESMO(SUA APP) QUISER MANTER O CONTROLE SOBRE OS ACESSOS
        player: Player,
        context: Context,
//        onAuthenticationSuccess: (BiometricPrompt.AuthenticationResult) -> Unit,
        onAuthenticationError: (Int, String) -> Unit,
        onResult: (Player) -> Unit,
    ) {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val prompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
//                Timber.d("Authenticado com sucesso, acesso permitido!")
//                onAuthenticationSuccess(result)
                onResult.invoke(player)
            }

            override fun onAuthenticationError(errorCode: Int, errorMessage: CharSequence) {
//                Timber.d("Acesso negado! Alguem ta tentando usar teu celular!")
                onAuthenticationError(errorCode, errorMessage.toString())
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .apply { if (message != null) setDescription(message) }
            .setConfirmationRequired(confirmationRequired)
            .setDeviceCredentialAllowed(true)
            .build()

        initializedCipher?.let {
            val cryptoObject = BiometricPrompt.CryptoObject(initializedCipher)
            prompt.authenticate(promptInfo, cryptoObject)
            return
        }

        prompt.authenticate(promptInfo)

    }

    fun caAuthenticate(fragment: Fragment) {


        //        val biometricManager = BiometricManager.from(context)
//        val canAuthenticate : Boolean =
//            when (biometricManager.canAuthenticate()) {
//            BiometricManager.BIOMETRIC_SUCCESS -> true
//            else -> false
//        }


    }

    fun Fragment.dialogConfirm(title: String, onResult: () -> Unit) {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setPositiveButton("Sim") { dialog, which ->
                onResult.invoke()
            }.setNeutralButton("cancelar") { dialog, wich ->
            }.show()

    }

}