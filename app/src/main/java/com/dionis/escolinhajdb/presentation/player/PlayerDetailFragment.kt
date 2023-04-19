package com.dionis.escolinhajdb.presentation.player

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentPlayerDetailBinding
import com.dionis.escolinhajdb.presentation.pdf.FromPdfSaveFragment.Companion.KEY
import com.dionis.escolinhajdb.presentation.pdf.FromPdfSaveFragment.Companion.PLAYERTOPDF
import com.dionis.escolinhajdb.util.Extensions.datePicker
import com.dionis.escolinhajdb.util.Extensions.openKeyboard
import com.dionis.escolinhajdb.util.Extensions.toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.slider.Slider
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@AndroidEntryPoint
class PlayerDetailFragment : Fragment() {

    private lateinit var binding: FragmentPlayerDetailBinding


    private lateinit var playerDetail: Player
    private val viewModel: PlayerViewModel by viewModels()
    private var genreList = listOf("Masculino", "Feminino")
    private var playerGenre = ""
    private var playerPositionList = listOf("Lateral direito", "Lateral esquerdo", "Goleiro", "Centroavante", "--adicionar--")
    private var playerPosition = ""
    private var bloodTypeList = listOf("A+", "B+", "A-", "B-", "AB+", "AB-", "O+", "O-")
    private var playerBloodType = ""
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


        ageFormatter()
        //getPlayerObj()
        setDatePicker()
        loadImage()
        setUpClicks()
        setInfo()
        uploadImage2()
        editActivating(false)


    }


    private fun ageFormatter() {
        if (playerDetail.playersBirth.isNotEmpty()) {
            val imputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val birthDate = LocalDate.parse(playerDetail.playersBirth, imputFormat) // Converter a string em um objeto LocalDate
            val formattedBirthDate = birthDate.format(outputFormat) // Formatar a data no padrão "yyyy-MM-dd"
            val dateForCalc = LocalDate.parse(formattedBirthDate, outputFormat)
            val today = LocalDate.now()
            age = ChronoUnit.YEARS.between(dateForCalc, today).toInt()
        }
    }

    private fun playerGenreSpinner() {


//        binding.spinnerGenre.post {
//            binding.spinnerGenre.performClick()
//
//        }
        val spinner = binding.spinnerGenre
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            genreList.map { it })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        val spinnerPosition = adapter.getPosition(binding.tvGenre.text.toString())
        spinner.setSelection(spinnerPosition)
        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemview: View?,
                    position: Int,
                    id: Long,
                ) {
                    playerGenre = genreList[position]
                    binding.tvGenre.text = playerGenre
//                    binding.tvGenre.visibility = View.VISIBLE
//                    binding.cvGenre.visibility = View.INVISIBLE
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
    }

    private fun playerPositionSpinner() {

        //   binding.spinnerPlayerPosition.post { binding.spinnerPlayerPosition.performClick() }

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            playerPositionList.map { it })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPlayerPosition.adapter = adapter
        val spinnerPosition = adapter.getPosition(binding.tvPlayerPosition.text.toString())
        binding.spinnerPlayerPosition.setSelection(spinnerPosition)

        binding.spinnerPlayerPosition.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemview: View?,
                    position: Int,
                    id: Long,
                ) {
                    val selectedPlayerPosition = playerPositionList[position]
                    if (selectedPlayerPosition == playerPositionList.last()) {
                        toast("clicado")
                    } else {
                        playerPosition = selectedPlayerPosition
                        binding.tvPlayerPosition.text = playerPosition
//                        binding.tvPlayerPosition.visibility = View.VISIBLE
//                        binding.cvPlayerPosition.visibility = View.INVISIBLE
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }

    }

    private fun playerBloodSpinner() {

        //binding.spinnerBlood.post { binding.spinnerBlood.performClick() }

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            bloodTypeList.map { it })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBlood.adapter = adapter
        val spinnerPosition = adapter.getPosition(binding.tvBloodType.text.toString())
        binding.spinnerBlood.setSelection(spinnerPosition)


        binding.spinnerBlood.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemview: View?,
                    position: Int,
                    id: Long,
                ) {
                    val selectedBloodType = bloodTypeList[position]
                    playerBloodType = selectedBloodType
                    binding.tvBloodType.text = playerBloodType
//                    binding.tvBloodType.visibility = View.VISIBLE
//                    binding.spinnerBlood.visibility = View.INVISIBLE
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }

    private fun slider(view: EditText) {

        val sliderView = binding.slider

        view.setOnClickListener {
            sliderView.visibility = View.VISIBLE
        }

        sliderView.value = playerDetail.weight

        sliderView.setLabelFormatter { value: Float ->
            val format = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.getDefault()))
            format.positiveSuffix = "Kg"
            format.format(value.toDouble())

        }
        sliderView.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                sliderView.background.setTint(resources.getColor(R.color.jdb_ultra_light))
            }

            override fun onStopTrackingTouch(slider: Slider) {
                sliderView.background.setTint(Color.WHITE)
                sliderView.visibility = View.INVISIBLE
            }
        })

        sliderView.addOnChangeListener { slider, value, fromUser ->
            view.setText(value.toString())


        }


    }

    private fun turnVisible() = binding.apply {

        tvGenre.visibility = View.INVISIBLE
        cvGenre.visibility = View.VISIBLE

        tvBloodType.visibility = View.INVISIBLE
        cvBloodType.visibility = View.VISIBLE

        tvPlayerPosition.visibility = View.INVISIBLE
        cvPlayerPosition.visibility = View.VISIBLE

        tvResponsibleName.visibility = View.INVISIBLE
        cvResponsibleName.visibility = View.VISIBLE

        tvContact.visibility = View.INVISIBLE
        cvContact.visibility = View.VISIBLE

        tvPlayerBirth.visibility = View.INVISIBLE
        cvBirth.visibility = View.VISIBLE

        cvBirth.elevation = 20F
        cvBirth.radius = 2F



    }

    private fun setUpClicks() {

        binding.playerWeightEdt.setOnClickListener {
            slider(binding.playerWeightEdt)
        }

        binding.playerHeightEdt.setOnClickListener {
            slider(binding.playerHeightEdt)
        }


        binding.playerAgeTv.setOnClickListener {
            turnVisible()
            editActivating(true)
            playerGenreSpinner()
            playerPositionSpinner()
            playerBloodSpinner()

        }

        binding.btnSave.setOnClickListener {
            //uploadImage()
            updatePlayer()
            findNavController().popBackStack()
        }

        binding.exportPdf.setOnClickListener {
            val args = Bundle()
            args.putParcelable(PLAYERTOPDF, playerDetail)
            args.putString(KEY, "export")
            findNavController().navigate(R.id.action_playerDetailFragment_to_fromPdfSaveFragment, args)
        }

        binding.savePdf.setOnClickListener {
            val args = Bundle().apply {
                putParcelable(PLAYERTOPDF, playerDetail)
                putString(KEY, "save")
            }
            findNavController().navigate(R.id.action_playerDetailFragment_to_fromPdfSaveFragment, args)
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

    private fun editActivating(isClickable: Boolean) = binding.apply {

        playerNameEdt.isClickable = isClickable
        playerWeightEdt.isClickable = isClickable
        playerHeightEdt.isClickable = isClickable
        tvGenre.isClickable = isClickable
        tvBloodType.isClickable = isClickable
        tvPlayerPosition.isClickable = isClickable
        cvResponsibleName.isClickable = isClickable
        responsibleTypeEdt.isClickable = isClickable
        cvContact.isClickable = isClickable
        tvContact.isFocusable = isClickable
        playerBirthEdt.isClickable = isClickable
        cvBirth.isClickable = isClickable



    }


    private fun setInfo() = binding.apply {

        playerDetail.let {

            playerNameEdt.setText(it.playerName)
            tvPlayerPosition.text = it.position

            if (it.weight > 0.1) {
                playerWeightEdt.setText(it.weight.toString())
            }
            if (it.height > 0.1) {
                playerHeightEdt.setText(it.height.toString())
            }
            responsibleNameEdt.setText(it.responsibleName)
            tvResponsibleName.text = it.responsibleName
            responsibleTypeEdt.setText(it.responsibleType)

            tvPlayerBirth.text = it.playersBirth
            playerBirthEdt.text = it.playersBirth

            playerAgeTv.text = age.toString().plus(" Anos")

            tvBloodType.text = it.bloodType

            healthNotesEdt.setText(it.healthNotes)
            SkillsNotesEdt.setText(it.skillsNotes)
            if (it.genre.isNotEmpty()) {
                tvGenre.text = it.genre
                playerGenre = it.genre
            }
            if (playerDetail.images.isNotEmpty()) {
                Picasso.get().load(playerDetail.images[0]).into(binding.playerImg)
            }
            tvContact.setText(it.contacts)
            contactEdt.setText(it.contacts)
        }

    }


    private fun setDatePicker() {

        datePicker(playerDetail.playersBirth, binding.playerBirthEdt)


    }

    private fun updateLabel() {

        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.playerBirthEdt.setText(sdf.format(myCalendar.time).toString())
//        val simpleFormat = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.getDefault())
//        dateFormatted = simpleFormat.format(myCalendar.time)

        val currentDay = Calendar.getInstance()

        age = (
                currentDay.get(Calendar.YEAR) -
                        myCalendar.get(Calendar.YEAR))
        if (myCalendar.get(Calendar.DAY_OF_YEAR) > currentDay.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        binding.playerAgeTv.text = age.toString()


    }


    private fun getPlayerObj(): Player {

        var weight = 0f
        if (binding.playerWeightEdt.text.isNotEmpty()) {
            weight = binding.playerWeightEdt.text.toString().toFloat()
        }
        var height = 0f
        if (binding.playerHeightEdt.text.isNotEmpty()) {
            height = binding.playerHeightEdt.text.toString().toFloat()
        }

        return Player(
            id = playerDetail.id,
            playerName = binding.playerNameEdt.text.toString(),
            position = binding.tvPlayerPosition.text.toString(),
            responsibleName = binding.responsibleNameEdt.text.toString(),
            responsibleType = binding.responsibleTypeEdt.text.toString(),
            playersBirth = binding.playerBirthEdt.text.toString(),
            images = getimageUrls(),
            healthNotes = binding.healthNotesEdt.text.toString(),
            skillsNotes = binding.SkillsNotesEdt.text.toString(),
            weight = weight,
            height = height,
            genre = playerGenre,
            contacts = binding.contactEdt.unMasked,
            bloodType = binding.tvBloodType.text.toString()

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
                        viewModel.updatePlayer(getPlayerObj())
                        toast("suceso")
                        findNavController().popBackStack()
                    }
                }
            }
        } else {
            viewModel.updatePlayer(getPlayerObj())
        }

    }

    //testando
    private fun uploadImage2() {
        if (imageUris.isNotEmpty()) {
            viewModel.uploadSingleImage2(imageUris.first()) {
                when (it) {
                    is UiState.Loading -> {
                    }
                    is UiState.Failure -> {
                        toast(it.error)
                    }
                    is UiState.Success -> {
                        viewModel.updatePlayer(getPlayerObj())
                        toast("suceso")
                        findNavController().popBackStack()
                    }
                }
            }
        } else {
            viewModel.updatePlayer(getPlayerObj())
        }

    }


    companion object {
        const val PLAYER = "player"

    }

}