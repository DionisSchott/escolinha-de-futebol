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
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.States
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentRegisterPlayerBinding
import com.dionis.escolinhajdb.presentation.home.HomeActivity
import com.dionis.escolinhajdb.presentation.lists.ListViewModel
import com.dionis.escolinhajdb.util.Extensions.datePicker
import com.dionis.escolinhajdb.util.Extensions.datePickerReturn
import com.dionis.escolinhajdb.util.Extensions.firstName
import com.dionis.escolinhajdb.util.Extensions.spinnerAutoComplete
import com.dionis.escolinhajdb.util.Extensions.toast
import com.dionis.escolinhajdb.util.Genre.GENRE_LIST
import com.dionis.escolinhajdb.util.Permissions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RegisterPlayerFragment : Fragment() {

    val TAG: String = "RegisterPlayerFragment"
    private lateinit var binding: FragmentRegisterPlayerBinding
    private val viewModel: PlayerViewModel by activityViewModels()
    private val listViewModel: ListViewModel by activityViewModels()
    var objPlayer: Player? = null
    var imageUris: MutableList<Uri> = arrayListOf()
    var image: MutableList<String> = arrayListOf()
    private var categoryList = listOf<String>()
    var date = Calendar.getInstance().time

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission(), ::handlePermissionResult)


    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data!!
            imageUris.add(fileUri)
            Picasso.get().load(imageUris[0]).into(binding.imgPlayer)

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            toast(ImagePicker.getError(data))
        } else {
            Log.e(TAG, "Task Cancelled")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterPlayerBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as HomeActivity).showBottomNavigation(true)
    }

    private fun handlePermissionResult(isGranted: Boolean) {
        val permission = Permissions()

        val permissionState = permission.checkPermissionState(
            requireActivity(),
            READ_EXTERNAL_STORAGE
        )
        when (permissionState) {
            Permissions.PermissionState.GRANTED -> loadImage()
            Permissions.PermissionState.DENIED ->
                requestPermission()
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listViewModel.getLists()
    }

    private fun setUp() {
        setObservers()
        setupClick()
        getInsertionDate()
        lifecycleScope.launch {
            delay(500)
            spinner()
        }
        (activity as HomeActivity).showBottomNavigation(false)


    }

//    }

    private fun validateFields() {

        val playerName = binding.edtPlayerName.text.toString()
        val responsibleName = binding.edtResponsibleName.text.toString()
        val playersBirth = binding.edtPlayersBirth.text.toString()
        val playerGenre = binding.genre.text.toString()
        val playerCategory = binding.category.text.toString()

        viewModel.validateFields(playerName, responsibleName, playersBirth, playerGenre, playerCategory)
    }

    private fun loadImage() {
        ImagePicker.with(this)
            .compress(1024)
            .galleryOnly()
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    private fun setObservers() {

        viewModel.validateFields.observe(viewLifecycleOwner) {
            when (it) {
                is States.ValidateRegisterPlayer.PlayerNameEmpty -> {
                    showObligatoryField(binding.edtPlayerNameLayout, R.string.obligatory_field)
                }
                is States.ValidateRegisterPlayer.ResponsibleNameEmpty -> {
                    showObligatoryField(binding.edtResponsibleLayout, R.string.obligatory_field)
                }
                is States.ValidateRegisterPlayer.PlayersBirthEmpty -> {
                    showObligatoryField(binding.edtPlayersBirthLayout, R.string.obligatory_field)
                }
                is States.ValidateRegisterPlayer.PlayerGenreEmpty -> {
                    showObligatoryField(binding.cvGenre, R.string.obligatory_field)
                }
                is States.ValidateRegisterPlayer.PlayerCategoryEmpty -> {
                    showObligatoryField(binding.cvCategory, R.string.obligatory_field)
                }

                is States.ValidateRegisterPlayer.FieldsDone -> {
                    uploadImage()
                    // registerPlayer()
                    showDialog()
                }
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
                    binding.load.visibility = View.VISIBLE
                }
                is UiState.Failure -> {
                    binding.load.visibility = View.INVISIBLE
                    toast(it.error)
                }
                is UiState.Success -> {
                    binding.load.visibility = View.INVISIBLE
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
    }

    private fun getPlayerObj(): Player {
        return Player(
            id = "",
            playerName = binding.edtPlayerName.text.toString(),
            preferredName = firstName(binding.edtPlayerName.text.toString()),
            responsibleName = binding.edtResponsibleName.text.toString(),
            playersBirth = binding.edtPlayersBirth.text.toString(),
            images = image,
            responsibleType = binding.edtResponsibleType.text.toString(),
            genre = binding.genre.text.toString(),
            startDate = date,
            category = binding.category.text.toString(),
        )
    }

    private fun getInsertionDate() {

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.edtInsertionDate.setText(formatter.format(date))

        datePickerReturn("", binding.edtInsertionDate) {
            date = it
            binding.edtInsertionDate.setText(formatter.format(it))
        }

    }

    private fun setupClick() = binding.apply {

        edtPlayersBirthLayout.setOnClickListener {
            cvBirth.visibility = View.VISIBLE
        }
        datePicker("", edtPlayersBirth)


        imgPlayer.setOnClickListener {
            tryLoadImage()
        }
        btnDone.setOnClickListener {
            validateFields()
            //    uploadImage()
        }
        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }


    private fun getImageUrls(): List<String> {
        if (imageUris.isNotEmpty()) {
            return imageUris.map { it.toString() }
        } else {
            return objPlayer?.images ?: arrayListOf()
        }
    }

    private fun showObligatoryField(edt: TextInputLayout, message: Int) {
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

        dialog.show()
    }


    private fun spinner() {
        spinnerAutoComplete(binding.genre, GENRE_LIST)
        spinnerAutoComplete(binding.category, categoryList)
    }


    private fun uploadImage() {
        if (imageUris.isNotEmpty()) {
            viewModel.uploadSingleImage(imageUris.first()) {
                when (it) {
                    is UiState.Loading -> {
                    }
                    is UiState.Failure -> {
                        toast(it.error)
                    }
                    is UiState.Success -> {
                        image.add(it.data.toString())
                        registerPlayer()
                        toast("suceso")
                        findNavController().popBackStack()
                    }
                }
            }
        } else {
            registerPlayer()
        }

    }

}