package com.dionis.escolinhajdb.presentation.coach

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.databinding.FragmentRegisterBinding
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.presentation.auth.ViewModel
import com.dionis.escolinhajdb.presentation.home.HomeActivity
import com.dionis.escolinhajdb.presentation.lists.ListViewModel
import com.dionis.escolinhajdb.util.Extensions.datePicker
import com.dionis.escolinhajdb.util.Extensions.loadImage
import com.dionis.escolinhajdb.util.Extensions.toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class RegisterCoachFragment : Fragment() {

    val TAG: String = "UserUpdateFragment"
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: ViewModel by viewModels()
    private val listViewModel: ListViewModel by viewModels()
    private val myCalendar = Calendar.getInstance()
    private var functionList = listOf<String>()
    private var categoryList = listOf<String>()

    var imageUri: Uri? = null
    var image: String = ""

    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            imageUri = data?.data!!
            Picasso.get().load(imageUri).into(binding.coachImg)

        } else if (resultCode == ImagePicker.RESULT_ERROR) {

            toast(ImagePicker.getError(data))
        } else {

            Log.e(TAG, "Task Cancelled")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listViewModel.getLists()
        setUp()

    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as HomeActivity).showBottomNavigation(true)
    }

    private fun setUp() {
        setDatePicker()
        setUpClicks()
        (activity as HomeActivity).showBottomNavigation(false)
    }

    private fun setDatePicker() {
        datePicker("", binding.tvBirth)
    }

    private fun setUpClicks() = binding.apply {

        coachImg.setOnClickListener { loadImage() }
        btnBack.setOnClickListener { findNavController().popBackStack() }
        btnDone.setOnClickListener { uploadImage() }
    }


    private fun register() {

        observer()
        if (validate()) {
            viewModel.register(
                email = binding.edtEmail.text.toString(),
                password = binding.edtPassword.text.toString(),
                coach = getCoachObj()
            )
            findNavController().popBackStack()
        }

    }

    private fun observer() {
        listViewModel.lists.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {

                }
                is UiState.Failure -> {

                }
                is UiState.Success -> {
                    functionList = it.data.function
                    categoryList = it.data.subFunction
                }
            }
        }

        viewModel.register.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.btnDone.setText("")
                }
                is UiState.Failure -> {
                    binding.btnDone.setText("Erro")
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

        val user = FirebaseAuth.getInstance().currentUser?.uid

        return Coach(
            id = "",
            photo = image,
            name = binding.edtUserName.text.toString(),
            email = binding.edtEmail.text.toString(),
            function = binding.edtFunction.text.toString(),
            subFunction = binding.edtCategory.text.toString(),
            birth = binding.tvBirth.text.toString(),
            addedBy = user!!,
            memberSince = myCalendar.time.toString()


        )
    }

    private fun validate(): Boolean {
        var isValid = true


        if (binding.edtUserName.text.isNullOrEmpty()) {
            isValid = false
            toast("digite um nome")
        }

        if (binding.edtFunction.text.isNullOrEmpty()) {
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
        if (binding.edtPassword.text.isNullOrEmpty()) {
            isValid = false
            toast("digite uma senha")
        } else {
            if (binding.edtPassword.text.toString().length < 8) {
                isValid = false
                toast("senha curta")
            }
        }
        return isValid

    }

    private fun loadImage() {
        loadImage(startForProfileImageResult)
    }

    private fun uploadImage() {
        if (imageUri != null) {
            viewModel.uploadImage(imageUri!!) {
                when (it) {
                    is UiState.Loading -> {
                    }
                    is UiState.Failure -> {
                        toast(it.error)
                    }
                    is UiState.Success -> {
                        image = it.data.toString()
                        register()
                        toast(getString(R.string.successfully_added))
                        findNavController().popBackStack()
                    }
                }
            }
        } else {
            register()
        }

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