package com.dionis.escolinhajdb.presentation.player

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.States
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentRegisterPlayer2Binding
import com.dionis.escolinhajdb.presentation.home.HomeActivity
import com.dionis.escolinhajdb.presentation.lists.ListViewModel
import com.dionis.escolinhajdb.util.Extensions.datePicker
import com.dionis.escolinhajdb.util.Extensions.firstName
import com.dionis.escolinhajdb.util.Extensions.loadImage
import com.dionis.escolinhajdb.util.Extensions.spinnerAutoComplete
import com.dionis.escolinhajdb.util.Extensions.toast
import com.dionis.escolinhajdb.util.Permissions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class RegisterPlayerFragment : Fragment() {

    val TAG: String = "RegisterPlayerFragment"
    private lateinit var binding: FragmentRegisterPlayer2Binding
    private val viewModel: PlayerViewModel by viewModels()
    private val listViewModel: ListViewModel by activityViewModels()
    private var genreList = listOf("Masculino", "Feminino")
    var objPlayer: Player? = null
    var imageUris: MutableList<Uri> = arrayListOf()
    var image: String = ""
    var imageUri: Uri? = null
    var date = Calendar.getInstance().time
    private var bloodTypeList: List<String> = arrayListOf()
    private var playerPositionList: List<String> = arrayListOf()
    private var playerCategoryList: List<String> = arrayListOf()
    private var categoryList = listOf<String>()

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission(), ::handlePermissionResult)

    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data!!
            imageUris.add(fileUri)
            imageUri = fileUri
            Picasso.get().load(imageUris[0]).into(binding.playerImg)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            toast(ImagePicker.getError(data))
        } else {
            Log.e(TAG, "Task Cancelled")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterPlayer2Binding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        listViewModel.getLists()
        setUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as HomeActivity).showBottomNavigation(true)
    }

    private fun setUp() {
        setObservers()
        setupClick()
//        getInsertionDate()
        lifecycleScope.launch {
            delay(500)
            setupSpinner()
        }
        (activity as HomeActivity).showBottomNavigation(false)

    }

    private fun handlePermissionResult(isGranted: Boolean) {
        val permission = Permissions()

        val permissionState = permission.checkPermissionState(
            requireActivity(),
            READ_EXTERNAL_STORAGE
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
        requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
    }

    private fun tryLoadImage() {
        val permissions = Permissions()
        permissions.requestPermission(requireActivity(), READ_EXTERNAL_STORAGE, { loadImage() }, { requestPermission() })

    }

    private fun setupSpinner() {
        spinnerAutoComplete(binding.tvGenre, genreList)
        spinnerAutoComplete(binding.tvBloodType, bloodTypeList)
        spinnerAutoComplete(binding.playerCategory, categoryList)
    }

    private fun validateFields() {

        val playerName = binding.edtPlayerName.text.toString()
        val responsibleName = binding.edtResponsibleName.text.toString()
        val playersBirth = binding.edtPlayersBirth.text.toString()
        val playerGenre = binding.tvGenre.text.toString()
        val playerCategory = binding.playerCategory.text.toString()

        viewModel.validateFields(playerName, responsibleName, playersBirth, playerGenre, "playerCategory")
    }

    private fun loadImage() {
        loadImage(startForProfileImageResult)
    }

    private fun setObservers() {

        listViewModel.lists.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Failure -> {
                    toast("falha ao carregar listas")
                }

                is UiState.Loading -> {
                }

                is UiState.Success -> {
                    bloodTypeList = it.data.blood
                    playerPositionList = it.data.position
                    playerCategoryList = it.data.category
                    setupSpinner()
                }

            }
        }

        viewModel.validateFields.observe(viewLifecycleOwner) {
            when (it) {
                is States.ValidateRegisterPlayer.PlayerNameEmpty -> {
                    showObligatoryField(binding.edtPlayerName, R.string.obligatory_field)
                }

                is States.ValidateRegisterPlayer.ResponsibleNameEmpty -> {
                    showObligatoryField(binding.edtResponsibleName, R.string.obligatory_field)
                }

                is States.ValidateRegisterPlayer.PlayersBirthEmpty -> {
                    showObligatoryField(binding.edtPlayersBirth, R.string.obligatory_field)
                }

                is States.ValidateRegisterPlayer.PlayerGenreEmpty -> {
                    showObligatoryField(binding.tvGenre, R.string.obligatory_field)
                }
//                is States.ValidateRegisterPlayer.PlayerCategoryEmpty -> {
//                    showObligatoryField(binding.tvCategory, R.string.obligatory_field)
//                }
                else -> uploadImage()

            }
        }

        listViewModel.lists.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {

                }

                is UiState.Failure -> {

                }

                is UiState.Success -> {
                    categoryList = it.data.category
                }
            }
        }

        viewModel.playerRegister.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
//                    binding.load.visibility = View.VISIBLE
                }

                is UiState.Failure -> {
//                    binding.load.visibility = View.INVISIBLE
                    toast(it.error)
                }

                is UiState.Success -> {
//                    binding.load.visibility = View.INVISIBLE
                    objPlayer = it.data.first
                    findNavController().popBackStack()
                }
            }
        }

    }

    private fun registerPlayer() {
        viewModel.registerPlayer(
            player = getPlayerObj()
        )
        showDialog()
    }

    private fun getPlayerObj(): Player {

        val coach = FirebaseAuth.getInstance().currentUser?.uid

        return Player(
            id = "",
            playerName = binding.edtPlayerName.text.toString(),
            preferredName = firstName(binding.edtPlayerName.text.toString()),
            responsibleName = binding.edtResponsibleName.text.toString(),
            playersBirth = binding.edtPlayersBirth.text.toString(),
            images = image,
            responsibleType = binding.responsibleTypeEdt.text.toString(),
            genre = binding.tvGenre.text.toString(),
            startDate = date,
            contacts = binding.contactEdt.unMasked,
            category = binding.playerCategory.text.toString(),
            addedBy = coach!!,
            healthNotes = binding.healthNotesEdt.text.toString(),
            skillsNotes = binding.SkillsNotesEdt.text.toString()
        )
    }

//    private fun getInsertionDate() {
//
//
//        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
//        binding.edtInsertionDate.setText(formatter.format(date))
//
//        datePickerReturn("", binding.edtInsertionDate) {
//            date = convertLocalDateToDate(it)
//            binding.edtInsertionDate.setText(formatter.format(date))
//        }
//
//    }

    private fun setupClick() = binding.apply {

        cvBirth.setOnClickListener {
            cvBirth.visibility = View.VISIBLE
        }
        datePicker("", edtPlayersBirth)


        playerImg.setOnClickListener {
            tryLoadImage()
        }
        btnDone.setOnClickListener {
            validateFields()
            //    uploadImage()
        }
        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

//        btnBack.setOnClickListener {
//            findNavController().popBackStack()
//        }

    }


//    private fun getImageUrls(): List<String> {
//        if (imageUris.isNotEmpty()) {
//            return imageUris.map { it.toString() }
//        } else {
//            return objPlayer?.images ?: arrayListOf()
//        }
//    }

    private fun showObligatoryField(edt: EditText, message: Int) {
        edt.error = getString(message)
    }

    private fun showDialog() {

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_layout)
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        val button = dialog.findViewById<TextView>(R.id.button)
        button.setOnClickListener {
            dialog.dismiss()
        }
//        val navigate = dialog.findViewById<TextView>(R.id.buttonNavigate)
//        navigate.setOnClickListener {
//            dialog.dismiss()
//        }

        dialog.show()
    }


    private fun spinner() {

//        setSpinner(binding.spinnerGenre, genreList, binding.tvGenre)
//        setSpinner(binding.spinnerBlood, genreList, binding.tvCategory)
//        spinnerAutoComplete(binding.genre, GENRE_LIST)
//        spinnerAutoComplete(binding.category, categoryList)
    }


    private fun uploadImage() {
        if (imageUri != null) {
            viewModel.uploadSingleImage(imageUri!!) {
                when (it) {
                    is UiState.Loading -> {
                    }

                    is UiState.Failure -> {
                        toast(it.error)
                    }

                    is UiState.Success -> {
                        image = it.data.toString()
                        registerPlayer()
                        toast("sucesso")

                    }
                }
            }
        } else {
            registerPlayer()
        }

    }

}