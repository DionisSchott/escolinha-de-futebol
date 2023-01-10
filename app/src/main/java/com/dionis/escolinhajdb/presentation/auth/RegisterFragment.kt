package com.dionis.escolinhajdb.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.databinding.FragmentRegisterBinding
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.util.Extensions.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: ViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()

    }

    private fun setUp() {
        setUpClicks()
    }

    private fun setUpClicks() {

        register()

    }




    private fun register() {
        binding.btnDone.setOnClickListener {
 //           validate(it)
            observer()
            if (validate()){
                viewModel.register(
                    email = binding.edtEmail.text.toString(),
                    password = binding.edtPasswordConfirm.text.toString(),
                    coach = getCoachObj()
                )
                findNavController().popBackStack()
            }
        }
    }

    private fun observer() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when(state){
                is UiState.Loading -> {
                    binding.btnDone.setText("")
                }
                is UiState.Failure -> {
                    binding.btnDone.setText("Register")
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.btnDone.setText("Register")
                    toast(state.data)
                    findNavController().popBackStack()
                }
            }
        }
    }


    private fun getCoachObj(): Coach {
        return Coach(
            id = "",
            name = binding.edtUserName.text.toString(),
            category = binding.edtCategory.text.toString(),
            email = binding.edtEmail.text.toString(),
        )
    }

    private fun validate(): Boolean {
        var isValid = true


        if (binding.edtUserName.text.isNullOrEmpty()) {
            isValid = false
            toast("digite um nome")
        }

        if (binding.edtCategory.text.isNullOrEmpty()) {
            isValid = false
            toast("digite a categoria que treina")
        }

        if (binding.edtEmail.text.isNullOrEmpty()) {
            isValid = false
            toast("preencha um e-mail")
        } else {
            if (!binding.edtEmail.text.toString().isValidEmail()) {
                isValid = false
                toast("e-mail inválido")
            }
        }
        if (binding.edtPasswordConfirm.text.isNullOrEmpty()){
            isValid = false
            toast("digite uma senha")
        }else{
            if (binding.edtPasswordConfirm.text.toString().length < 8){
                isValid = false
                toast("senha curta")
            }
        }

        return isValid


    }





    private fun String.isValidEmail() =
        isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()


//    private fun validate(view: View) {
//
//        val email = binding.edtEmail.text.toString()
//        val password = binding.edtPassword.text.toString()
//
//        if (email.isEmpty() || password.isEmpty()) {
//            val snackbar = Snackbar.make(view, "preencha todos os campos", Snackbar.LENGTH_SHORT)
//            snackbar.setBackgroundTint(Color.RED)
//            snackbar.show()
//        } else {
//            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { cadastro ->
//                if (cadastro.isSuccessful) {
//                    Toast.makeText(context, "cadastrado com sucesso", Toast.LENGTH_SHORT).show()
//                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
//                }
//            }.addOnFailureListener {
//                val errorMessage = when (it) {
//                    is FirebaseAuthWeakPasswordException -> "senha muito curta"
//                    is FirebaseAuthInvalidCredentialsException -> "e-mail inválido"
//                    is FirebaseAuthUserCollisionException -> "e-mail já cadastrado"
//                    is FirebaseNetworkException -> "sem conexão com a internet"
//                    else -> "erro ao cadastrar"
//                }
//                val snackbar = Snackbar.make(view, errorMessage, Snackbar.LENGTH_SHORT)
//                snackbar.setBackgroundTint(Color.RED)
//                snackbar.show()
//            }
//        }
//    }

}