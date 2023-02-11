package com.dionis.escolinhajdb.presentation.player

import android.R
import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
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
import com.dionis.escolinhajdb.R.style
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentPlayerDetailBinding
import com.dionis.escolinhajdb.util.Extensions.toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PlayerDetailFragment : Fragment() {

    private lateinit var binding: FragmentPlayerDetailBinding
    private lateinit var dateFormatted: String
    private lateinit var playerDetail: Player
    private val viewModel: PlayerViewModel by viewModels()
    private var genreList = listOf("Masculino", "Feminino")
    private var genre = ""
    private val myCalendar = Calendar.getInstance()
//    lateinit var imageUri: Uri
    val TAG: String = "NoteDetailFragment"
    var imageUris: MutableList<Uri> = arrayListOf()
    var age: Int = 0

    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data!!
            imageUris.add(fileUri)
            //   imageUri = fileUri
            Picasso.get().load(imageUris[0]).into(binding.playerImg)
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
        binding = FragmentPlayerDetailBinding.inflate(inflater, container, false)

        playerDetail = arguments?.getParcelable(PLAYER)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //getPlayerObj()
        clearText()
        setDatePicker()
        loadImage()
        setUpClicks()
        setUpSpinner()
        setInfo()
    }


    private fun setUpSpinner() {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            genreList.map { it })
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerGenre.adapter = adapter
        val spinnerPosition = adapter.getPosition(genre)
        binding.spinnerGenre.setSelection(spinnerPosition)

        binding.spinnerGenre.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemview: View?,
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


    private fun loadImage() {
        binding.playerImg.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)
                .galleryOnly()
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

    }


    private fun setInfo() {

        playerDetail.let {
            binding.playerNameEdt.setText(it.playerName)
            binding.playerPositionEdt.setText(it.position)

            if (it.weight > 0.1) {
                binding.playerWeightEdt.setText(it.weight.toString())
            }
            if (it.height > 0.1) {
                binding.playerHeightEdt.setText(it.height.toString())
            }
            binding.responsibleNameEdt.setText(it.responsibleName)
            binding.responsibleTypeEdt.setText(it.responsibleType)
            binding.playerBirthEdt.setText(it.playersBirth)
            binding.healthNotesEdt.setText(it.healthNotes)
            binding.SkillsNotesEdt.setText(it.skillsNotes)
            if (it.genre.isNotEmpty()) {
                binding.tvGenre.text = it.genre
                genre = it.genre
            }
            if (playerDetail.images.isNotEmpty()) {
                Picasso.get().load(playerDetail.images[0]).into(binding.playerImg)
            }
            binding.playerAgeTv.text = age.toString()
            binding.mskContact.setText(it.contacts)

        }

    }

    private fun clearText() {
        binding.playerWeightEdt.setOnClickListener {clearText()}
    }

    private fun setUpClicks() {
        binding.btnSave.setOnClickListener {
            //uploadImage()
            updatePlayer()
            findNavController().popBackStack()
        }
        binding.tvGenre.setOnClickListener {
            it.visibility = View.INVISIBLE
            binding.cvGenre.visibility = View.VISIBLE
        }
    }


    private fun setDatePicker() {

        val date = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, day)
            updateLabel()

        }

        binding.playerBirthEdt.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(), style.DatePickerBackGround_Jdb, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        }

    }

    private fun updateLabel() {

        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.playerBirthEdt.setText(sdf.format(myCalendar.time).toString())
        val simpleFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.getDefault())
        dateFormatted = simpleFormat.format(myCalendar.time)

        val currentDay = Calendar.getInstance()
        currentDay.time
        age = (currentDay.get(Calendar.YEAR) - myCalendar.get(Calendar.YEAR))
        if (myCalendar.get(Calendar.DAY_OF_YEAR) > currentDay.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        binding.playerAgeTv.text = age.toString()


    }


    private fun getPlayerObj(): Player {

        var weight = 0f
        if(binding.playerWeightEdt.text.isNotEmpty()){
            weight = binding.playerWeightEdt.text.toString().toFloat()
        }
        var height = 0f
        if(binding.playerHeightEdt.text.isNotEmpty()){
            height = binding.playerHeightEdt.text.toString().toFloat()
        }

        return Player(
            id = playerDetail.id,
            playerName = binding.playerNameEdt.text.toString(),
            position = binding.playerPositionEdt.text.toString(),
            responsibleName = binding.responsibleNameEdt.text.toString(),
            responsibleType = binding.responsibleTypeEdt.text.toString(),
            playersBirth = binding.playerBirthEdt.text.toString(),
            images = getimageUrls(),
            healthNotes = binding.healthNotesEdt.text.toString(),
            skillsNotes = binding.SkillsNotesEdt.text.toString(),
            weight = weight ,
            height = height,
            genre = genre,
            contacts = binding.mskContact.unMasked

        )
    }

    private fun updatePlayer() {

        viewModel.updatePlayer(getPlayerObj())

    }


    private fun getimageUrls(): List<String> {
        if (imageUris.isNotEmpty()) {
            return imageUris.map { it.toString() }
        } else {
            return playerDetail.images ?: arrayListOf()
        }
    }


//    private fun uploadImage() {
//        if (imageUris.isNotEmpty()) {
//            viewModel.uploadSingleImage(imageUris.first()) {
//                when (it) {
//                    is UiState.Loading -> {
//                    }
//                    is UiState.Failure -> {
//                        toast(it.error)
//                    }
//                    is UiState.Success -> {
//                        viewModel.updatePlayer(getPlayerObj())
//                        toast("suceso")
//                        findNavController().popBackStack()
//                    }
//                }
//            }
//        } else {
//            viewModel.updatePlayer(getPlayerObj())
//        }
//
//    }


    companion object {
        const val PLAYER = "player"

    }

}