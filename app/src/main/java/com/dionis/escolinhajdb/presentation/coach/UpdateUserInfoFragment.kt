package com.dionis.escolinhajdb.presentation.coach


import android.Manifest
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.data.model.Lists
import com.dionis.escolinhajdb.databinding.FragmentUpdateUserInfoBinding
import com.dionis.escolinhajdb.presentation.auth.ViewModel
import com.dionis.escolinhajdb.presentation.home.HomeActivity
import com.dionis.escolinhajdb.presentation.lists.ListViewModel
import com.dionis.escolinhajdb.util.Extensions.datePicker
import com.dionis.escolinhajdb.util.Extensions.loadImage
import com.dionis.escolinhajdb.util.Extensions.spinnerAutoComplete
import com.dionis.escolinhajdb.util.Extensions.toast
import com.dionis.escolinhajdb.util.Permissions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class UpdateUserInfoFragment : Fragment() {

    val TAG: String = "UserUpdateFragment"
    private lateinit var binding: FragmentUpdateUserInfoBinding
    private lateinit var coach: Coach
    private val viewModel: ViewModel by activityViewModels()
    private val listViewModel: ListViewModel by activityViewModels()
    private val myCalendar = Calendar.getInstance()
    private lateinit var position: List<String>
    private var functionList = listOf<String>()
    private var categoryList = listOf<String>()
    private var function = ""
    private lateinit var lists: Lists
    var imageUri: Uri? = null
    var photo: String = ""

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission(), ::handlePermissionResult)


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
        binding = FragmentUpdateUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coach = arguments?.getParcelable(COACH)!!
        listViewModel.getLists()
        setup()

    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as HomeActivity).showBottomNavigation(true)
    }

    private fun setup() {
        setData()
        setUpClicks()
        endIconClick()
        setDatePicker()
        setObserver()
        lifecycleScope.launch {
            delay(500)
            setupSpinner()
        }
        (activity as HomeActivity).showBottomNavigation(false)
    }

    private fun setUpClicks() = binding.apply {
        btnDone.setOnClickListener {
            observer()
            uploadImage()
        }
        coachImg.setOnClickListener {
            tryLoadImage()
        }
        btnChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_updateUserInfoFragment_to_changePasswordFragment)
        }
    }

    private fun setData() = binding.apply {
        if (coach.photo.isNotEmpty()) {
            Picasso.get().load(coach.photo).into(coachImg)
        }
        edtUserName.setText(coach.name)
        mskContact.setText(coach.contact)
        function.setText(coach.function)
        //tvFunction.setText(coach.function)
        edtSubFunction.setText(coach.subFunction)
        edtBirth.setText(coach.birth)
    }

    private fun setObserver() {
        listViewModel.lists.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Failure -> {
                }
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    lists = it.data
                    functionList = it.data.function
                    categoryList = it.data.category
                }
            }
        }

    }

    private fun handlePermissionResult(isGranted: Boolean) {
        val permission = Permissions()

        val permissionState = permission.checkPermissionState(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        when (permissionState) {
            Permissions.PermissionState.GRANTED -> loadImage()
            Permissions.PermissionState.DENIED -> requestPermission()
            // se negar 2 vezes criar função para ir para configurações liberar manualmente
            Permissions.PermissionState.DO_NOT_ASK -> {
                toast("permissão necessária")
                // função para ir para configurações liberar manualmente
            }
            Permissions.PermissionState.RATIONALE -> {
                toast("permissão necessária")
                requestPermission()
            }
        }
    }

    private fun requestPermission() {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun tryLoadImage() {
        val permissions = Permissions()
        permissions.requestPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, { loadImage() }, { requestPermission() })

    }


    private fun getCoachObject(): Coach {

        photo = coach.photo

        return Coach(
            id = coach.id,
            name = binding.edtUserName.text.toString(),
            email = coach.email,
            photo = photo,
            function = binding.function.text.toString(),
//            function = binding.tvFunction.text.toString(),
            subFunction = binding.edtSubFunction.text.toString(),
            birth = binding.edtBirth.text.toString(),
            genre = coach.genre,
            contact = binding.mskContact.unMasked,
            memberSince = coach.memberSince
        )
    }

    private fun endIconClick() {

        binding.edtUserNameLayout.setEndIconOnClickListener {
            binding.edtUserName.text?.clear()
            binding.edtUserName.requestFocus()
        }
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun updateUserInfo() {
        viewModel.updateUserInfo(getCoachObject())
    }

//    private fun coachFunctionSpinner() {
//
//
//        binding.edtFunctionLayout.visibility = View.INVISIBLE
//        binding.cvFunctionSpinner.visibility = View.VISIBLE
//
//        binding.edtFunctionSpinner.post { binding.edtFunctionSpinner.performClick() }
//
//        val adapter: ArrayAdapter<String> = ArrayAdapter(
//            requireContext(),
//            android.R.layout.simple_spinner_item,
//            this.functionList.map { it })
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.edtFunctionSpinner.adapter = adapter
//        val spinnerPosition = adapter.getPosition(binding.tvFunction.text.toString())
//        binding.edtFunctionSpinner.setSelection(spinnerPosition)
//
//        binding.edtFunctionSpinner.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    parentView: AdapterView<*>?,
//                    selectedItemview: View?,
//                    position: Int,
//                    id: Long,
//                ) {
//                    val selectedPlayerPosition = functionList[position]
//                    function = selectedPlayerPosition
//                    binding.tvFunction.setText(function)
//                    binding.edtFunctionLayout.visibility = View.VISIBLE
//                    binding.cvFunctionSpinner.visibility = View.INVISIBLE
//
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//
//                }
//            }
//    }

    private fun setupSpinner() {
        spinnerAutoComplete(binding.function, functionList)
        spinnerAutoComplete(binding.edtSubFunction, categoryList)
    }

    private fun setDatePicker() {
        datePicker(coach.birth, binding.edtBirth)
    }

    private fun observer() {

        viewModel.updateInfo.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                }
                is UiState.Failure -> {
                    toast(it.error)
                }
                is UiState.Success -> {
                    toast("suceso")
                    findNavController().popBackStack()
                }
            }
        }
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
                        photo = it.data.toString()
                        updateUserInfo()
                        toast(getString(R.string.successfully_updated))
                        findNavController().popBackStack()
                    }
                }
            }
        } else {
            updateUserInfo()
        }

    }


    companion object {
        const val COACH = "coach"
    }

}