package com.dionis.escolinhajdb.presentation.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.databinding.FragmentLoginBinding
import com.dionis.escolinhajdb.presentation.home.HomeActivity
import com.dionis.escolinhajdb.util.Extensions.navTo
import com.dionis.escolinhajdb.util.Extensions.toast
import com.dionis.escolinhajdb.util.UserManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    val viewModel: ViewModel by viewModels()
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userManager = UserManager(requireContext())

        observer()
        setUp()
    }

    private fun setUp() {
        readDataUser()
        openRegisterScreen()
        setUpClicks()
    }


    private fun setUpClicks() {
        binding.btnLogin.setOnClickListener {
            validate(it)
            observer()
        }
    }

    private fun openRegisterScreen() {
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun openHomeFragment() {
        val intent = Intent(requireContext(), HomeActivity::class.java)
        startActivity(intent)
        activity?.finish()

    }


    private fun validate(view: View) {

        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            val snackbar = Snackbar.make(view, "preencha todos os campos", Snackbar.LENGTH_SHORT)
            snackbar.setBackgroundTint(Color.RED)
            snackbar.show()
        } else {
            viewModel.login(
                email = binding.edtEmail.text.toString(),
                password = binding.edtPassword.text.toString()
            )

        }
    }

    private fun observer() {
        viewModel.login.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.loginProgress.visibility = View.VISIBLE
                }
                is UiState.Failure -> {
                    binding.loginProgress.visibility = View.INVISIBLE
                    toast(it.error)
                }
                is UiState.Success -> {
                    binding.loginProgress.visibility = View.INVISIBLE
                    openHomeFragment()
                    saveDataUser()
                    lifecycleScope.launch {
                        userManager.saveUseruid(it.data)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val userLogged = FirebaseAuth.getInstance().currentUser
        if (userLogged != null) {
            openHomeFragment()

        }
    }

//    private fun autoLogin() {
//        if (!binding.edtEmail.text.isNullOrEmpty() && !binding.edtPassword.text.isNullOrEmpty()) {
//            val email = binding.edtEmail.text.toString()
//            val password = binding.edtPassword.text.toString()
//
//            viewModel.login(email, password)
//        }
//    }


    private fun saveDataUser() {

        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        val authenticated = binding.checkBox.isChecked

        lifecycleScope.launch() { userManager.saveDataUser(email, password, authenticated) }
    }

    private fun readDataUser() {
        lifecycleScope.launch {
            val user = userManager.readDataUser()

            binding.edtEmail.setText(user.email)
            binding.edtPassword.setText(user.password)
            binding.checkBox.isChecked = user.authenticated
        }

    }

}