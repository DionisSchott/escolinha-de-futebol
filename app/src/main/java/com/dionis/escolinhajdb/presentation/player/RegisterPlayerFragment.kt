package com.dionis.escolinhajdb.presentation.player

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.States
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentRegisterPlayerBinding
import com.dionis.escolinhajdb.util.Extensions.toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.awaitAll
import java.util.*

@AndroidEntryPoint
class RegisterPlayerFragment : Fragment() {

    val TAG: String = "NoteDetailFragment"
    private lateinit var binding: FragmentRegisterPlayerBinding
    private val viewModel: PlayerViewModel by viewModels()
    var objPlayer: Player? = null
    var imageUris: MutableList<Uri> = arrayListOf()
    private var genreList = listOf("Masculino", "Feminino")
    private var genre = ""


    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data!!
            imageUris.add(fileUri)
//            adapter.updateList(imageUris)
//            binding.progressBar.hide()
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
//            binding.progressBar.hide()
            toast(ImagePicker.getError(data))
        } else {
//            binding.progressBar.hide()
            Log.e(TAG, "Task Cancelled")
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUp()
    }

    private fun setUp() {
        setUpSpinner()
        loadImage()
        validateFields()
        setObservers()
    }

    private fun setUpSpinner() {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            genreList.map { it })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGenre.adapter = adapter
        val spinnerPosition = adapter.getPosition(genre)
        binding.spinnerGenre.setSelection(spinnerPosition)

        binding.spinnerGenre.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemview: View,
                    position: Int,
                    id: Long,
                ) {
                    val selectedGender = genreList[position]
                    genre = selectedGender
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
    }

    private fun validateFields() {
        binding.btnDone.setOnClickListener {

            val playerName = binding.edtPlayerName.text.toString()
            val responsibleName = binding.edtresponsibleName.text.toString()
            val playersBirth = binding.edtPlayersBirth.text.toString()


            viewModel.validateFields(playerName, responsibleName, playersBirth, genre)
        }
    }

    private fun loadImage() {
        binding.imgPlayer.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)
                .galleryOnly()
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
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
                    showObligatoryField(binding.edtPlayersBirthLayout, R.string.obligatory_field)
                }

                is States.ValidateRegisterPlayer.FieldsDone -> {
                    registerPlayer()
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
            responsibleName = binding.edtresponsibleName.text.toString(),
            playersBirth = binding.edtPlayersBirth.text.toString(),
            images = getimageUrls(),
            responsibleType = binding.edtResponsibleType.text.toString(),
            genre = genre,
            insertionDate = Date()
        )
    }


    private fun getimageUrls(): List<String> {
        if (imageUris.isNotEmpty()) {
            return imageUris.map { it.toString() }
        } else {
            return objPlayer?.images ?: arrayListOf()
        }
    }

    private fun showObligatoryField(edt: TextInputLayout, message: Int) {
        edt.error = getString(message)
    }

}