package com.dionis.escolinhajdb.presentation.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.databinding.FragmentChangePasswordBinding
import com.dionis.escolinhajdb.util.Extensions.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase


class ChangePasswordFragment : Fragment() {

    private val viewModel: ViewModel by activityViewModels()
    private var binding: FragmentChangePasswordBinding? = null
    lateinit var firebaseUser: FirebaseUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        return FragmentChangePasswordBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        setUp()
    }

    private fun setUp() {
        binding?.setUpClicks()
        binding?.passwordValidate()
    }


    private fun FragmentChangePasswordBinding.setUpClicks() {
        btnDone.setOnClickListener {
            binding?.authenticateUser()
        }

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    private fun FragmentChangePasswordBinding.passwordValidate() {

        val passwordTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val newPassword = newPasswordEdt.text.toString()
                val confirmPassword = newPasswordConfirmEdt.text.toString()

                if (newPassword.length < 8) {
                    messagePassword.text = "senha curta"
                } else {
                    messagePassword.text = ""
                    if (newPassword == confirmPassword) {
                        messagePasswordConfirm.text = ""
                        btnDone.isEnabled = true
                    } else {
                        messagePasswordConfirm.text = "confirme sua senha atual"
                        btnDone.isEnabled = false

                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        newPasswordEdt.addTextChangedListener(passwordTextWatcher)
        newPasswordConfirmEdt.addTextChangedListener(passwordTextWatcher)


    }

    private fun FragmentChangePasswordBinding.authenticateUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentPassword = currentPasswordEdt.text.toString()

        if (currentPassword.isNotEmpty()) {
            viewModel.authenticateUser(currentUser, currentPassword) {
                when (it) {
                    is UiState.Loading -> {
                    }
                    is UiState.Failure -> {
                        toast(it.error)
                    }
                    is UiState.Success -> {
                        changePassword()
                    }
                }
            }
        } else {
            toast("Digite sua senha atual")
        }
    }


    private fun FragmentChangePasswordBinding.changePassword() {

        val newPassword = newPasswordEdt.text.toString()

        viewModel.changePassword(firebaseUser, newPassword) {

            when (it) {
                is UiState.Loading -> {
                }
                is UiState.Failure -> {
                    toast(it.error)
                }
                is UiState.Success -> {
                    toast(it.data)
                    findNavController().popBackStack()
                }
            }
        }


    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()

    }


}