package com.dionis.escolinhajdb.presentation.player

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.States
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentRegisterPlayerBinding
import com.dionis.escolinhajdb.presentation.home.HomeActivity
import com.dionis.escolinhajdb.util.Extensions.datePicker
import com.dionis.escolinhajdb.util.Extensions.firstName
import com.dionis.escolinhajdb.util.Extensions.toast
import com.dionis.escolinhajdb.util.Genre.GENRE
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class RegisterPlayerFragment : Fragment() {

    val TAG: String = "RegisterPlayerfragment"
    private lateinit var binding: FragmentRegisterPlayerBinding
    private val viewModel: PlayerViewModel by activityViewModels()
    var objPlayer: Player? = null
    var imageUris: MutableList<Uri> = arrayListOf()
    var image: MutableList<String> = arrayListOf()


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

    private fun setUp() {
        spinner()
        setObservers()
        setupClick()
        (activity as HomeActivity).showBottomNavigation(false)

    }

//    private fun setUpSpinner() {
//
//        binding.edtGenre.setOnClickListener {
//            binding.spinnerGenre.visibility = View.VISIBLE
//        }
//
//        val spinner = binding.spinnerGenre
//        val adapter = ArrayAdapter(
//            requireContext(),
//            R.layout.spinner_item,
//            genreList.map { it })
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinner.adapter = adapter
//        val spinnerPosition = adapter.getPosition(genre)
//        spinner.setSelection(spinnerPosition)
//        spinner.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    parentView: AdapterView<*>?,
//                    selectedItemview: View?,
//                    position: Int,
//                    id: Long,
//                ) {
//                    val selectedGender = genreList[position]
//                    genre = selectedGender
//                    binding.edtGenre.setText(genre)
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                }
//
//            }
//    }

    private fun validateFields() {

        val playerName = binding.edtPlayerName.text.toString()
        val responsibleName = binding.edtResponsibleName.text.toString()
        val playersBirth = binding.edtPlayersBirth.text.toString()
        val playerGenre = binding.genre.text.toString()

        viewModel.validateFields(playerName, responsibleName, playersBirth, playerGenre)
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

                is States.ValidateRegisterPlayer.FieldsDone -> {
                    registerPlayer()
                    showDialog()
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
            insertionDate = Date()
        )
    }

    private fun setupClick() = binding.apply {

        edtPlayersBirthLayout.setOnClickListener {
            cvBirth.visibility = View.VISIBLE
        }
        datePicker("", edtPlayersBirth)

        imgPlayer.setOnClickListener {
            loadImage()
        }
        btnDone.setOnClickListener {
//            validateFields()
            uploadImage()
        }
        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

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

        val layoutText = binding.cvGenre
        val text = binding.genre

        val items= GENRE
        val itemsAdapter = ArrayAdapter(requireContext(), R.layout.items_list, items)
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