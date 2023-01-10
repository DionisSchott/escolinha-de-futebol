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
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.databinding.FragmentLoginBinding
import com.dionis.escolinhajdb.presentation.home.HomeActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    val viewModel: ViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
    }

    private fun setUp() {
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

    private fun observer(){
        viewModel.login.observe(viewLifecycleOwner) {
            when(it) {
                is UiState.Loading -> {
//                    binding.loginProgress.visibility = View.VISIBLE
                }
                is UiState.Failure -> {
                    binding.loginProgress.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(),it.error,Toast.LENGTH_LONG).show()
                }
                is UiState.Success -> {
                    binding.loginProgress.visibility = View.INVISIBLE
                    openHomeFragment()

                }
            }
        }
    }

}