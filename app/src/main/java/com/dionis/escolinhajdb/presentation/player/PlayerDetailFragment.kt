package com.dionis.escolinhajdb.presentation.player

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
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentPlayerDetailBinding
import com.dionis.escolinhajdb.presentation.home.HomeActivity
import com.dionis.escolinhajdb.presentation.lists.ListViewModel
import com.dionis.escolinhajdb.presentation.pdf.FromPdfSaveFragment.Companion.KEY
import com.dionis.escolinhajdb.presentation.pdf.FromPdfSaveFragment.Companion.PLAYERTOPDF
import com.dionis.escolinhajdb.util.Extensions.datePicker
import com.dionis.escolinhajdb.util.Extensions.setSpinner
import com.dionis.escolinhajdb.util.Extensions.toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

@AndroidEntryPoint
class PlayerDetailFragment : Fragment() {

    private lateinit var binding: FragmentPlayerDetailBinding


    private lateinit var playerDetail: Player
    private val viewModel: PlayerViewModel by activityViewModels()
    private var genreList = listOf("Masculino", "Feminino")
    private var playerGenre = ""
    private var playerPositionList: List<String> = arrayListOf()
    private var playerCategoryList: List<String> = arrayListOf()
    private var playerPosition = "---"
    private var bloodTypeList: List<String> = arrayListOf()
    private var playerBloodType = ""
    private val myCalendar = Calendar.getInstance()
    private val listViewModel: ListViewModel by activityViewModels()

    val TAG: String = "NoteDetailFragment"
    var imageUris: MutableList<Uri> = arrayListOf()
    var image: MutableList<String> = arrayListOf()
    var age: Int = 0

    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data!!
            imageUris.add(fileUri)
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
        binding = FragmentPlayerDetailBinding.inflate(inflater, container, false)

        playerDetail = arguments?.getParcelable(PLAYER)!!
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
        playerGenreSpinner()
        setUpClicks()
        ageFormatter()
        setDatePicker()
        setInfo()
        setObservers()
        editActivating(false)
        (activity as HomeActivity).showBottomNavigation(false)
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
                }

            }
        }
    }


    private fun setUpClicks() {

        binding.playerImg.setOnClickListener {
            loadImage()
        }

//        binding.playerWeightEdt.setOnClickListener {
//            slider(binding.playerWeightEdt)
//        }
//
//        binding.playerHeightEdt.setOnClickListener {
//            slider(binding.playerHeightEdt)
//        }

        binding.btnEdit.setOnClickListener {
            turnVisible()
            editActivating(true)
            setUpSpinner()
        }

        binding.btnSave.setOnClickListener {
            uploadImage()
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
        setSpinner(binding.spinnerGenre, genreList, binding.tvGenre)
    }

    private fun playerPositionSpinner() {
        setSpinner(binding.spinnerPlayerPosition, playerPositionList, binding.tvPlayerPosition)
    }

    private fun playerBloodSpinner() {
        setSpinner(binding.spinnerBlood, bloodTypeList, binding.tvBloodType)
    }

    private fun playerCategorySpinner() {
        setSpinner(binding.spinnerPlayerCategory, playerCategoryList, binding.tvPlayerCategory)
    }


//    private fun slider(view: EditText) {
//
//
//        val sliderView = binding.slider
//
//        sliderView.visibility = View.VISIBLE
//
//        sliderView.value = playerDetail.weight
//
//
//        sliderView.setLabelFormatter { value: Float ->
//            val format = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.getDefault()))
//            format.positiveSuffix = "Kg"
//            format.format(value.toDouble())
//
//        }
//        sliderView.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
//            override fun onStartTrackingTouch(slider: Slider) {
//                sliderView.background.setTint(resources.getColor(R.color.jdb_ultra_light))
//            }
//
//            override fun onStopTrackingTouch(slider: Slider) {
//                sliderView.background.setTint(Color.WHITE)
//                sliderView.visibility = View.INVISIBLE
//            }
//        })
//
//        sliderView.addOnChangeListener { slider, value, fromUser ->
//            view.setText(value.toString())
//
//        }
//    }

    private fun turnVisible() = binding.apply {

        tvPreferredPlayerName.visibility = View.INVISIBLE
        preferredPlayerNameEdt.visibility = View.VISIBLE

        tvGenre.visibility = View.INVISIBLE
        cvGenre.visibility = View.VISIBLE

        tvBloodType.visibility = View.INVISIBLE
        cvBloodType.visibility = View.VISIBLE

        tvPlayerPosition.visibility = View.INVISIBLE
        cvPlayerPosition.visibility = View.VISIBLE

        tvPlayerCategory.visibility = View.INVISIBLE
        cvPlayerCategory.visibility = View.VISIBLE

        tvResponsibleName.visibility = View.INVISIBLE
        cvResponsibleName.visibility = View.VISIBLE

        tvPlayerName.visibility = View.INVISIBLE
        cvPlayerName.visibility = View.VISIBLE

        tvContact.visibility = View.INVISIBLE
        cvContact.visibility = View.VISIBLE

        tvPlayerBirth.visibility = View.INVISIBLE
        cvBirth.visibility = View.VISIBLE

        cvBirth.elevation = 20F
        cvBirth.radius = 2F

        playerWeightEdt.elevation = 20F
        playerHeightEdt.elevation = 20F


    }

    private fun setUpSpinner() {
        //playerGenreSpinner()
        playerPositionSpinner()
        playerBloodSpinner()
        playerCategorySpinner()
    }


    private fun loadImage() {
        ImagePicker.with(this)
            .compress(1024)
            .galleryOnly()
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    private fun editActivating(isClickable: Boolean) = binding.apply {

        playerImg.isClickable = isClickable
        playerNameEdt.isEnabled = isClickable
        cvPreferredPlayerName.isClickable = isClickable
        preferredPlayerNameEdt.isClickable = isClickable
        playerWeightEdt.isEnabled = isClickable
        playerHeightEdt.isEnabled = isClickable
        tvGenre.isClickable = isClickable
        tvBloodType.isClickable = isClickable
        tvPlayerPosition.isClickable = isClickable
        tvPlayerCategory.isClickable = isClickable
        cvResponsibleName.isClickable = isClickable
        responsibleTypeEdt.isClickable = isClickable
        cvContact.isClickable = isClickable
        tvContact.isClickable = isClickable
        //   playerBirthEdt.isClickable = isClickable
        cvBirth.isClickable = isClickable
        healthNotesEdt.isEnabled = isClickable
        SkillsNotesEdt.isEnabled = isClickable


    }


    private fun setInfo() = binding.apply {

        playerDetail.let {

            preferredPlayerNameEdt.setText(it.preferredName)
            tvPreferredPlayerName.text = it.preferredName

            playerNameEdt.setText(it.playerName)
            tvPlayerName.text = it.playerName

            tvPlayerPosition.text = it.position
            tvPlayerCategory.text = it.category

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
            playerBirthEdt.setText(it.playersBirth)
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
            preferredName = binding.preferredPlayerNameEdt.text.toString(),
            position = binding.tvPlayerPosition.text.toString(),
            responsibleName = binding.responsibleNameEdt.text.toString(),
            responsibleType = binding.responsibleTypeEdt.text.toString(),
            playersBirth = binding.playerBirthEdt.text.toString(),
            images = image,
            healthNotes = binding.healthNotesEdt.text.toString(),
            skillsNotes = binding.SkillsNotesEdt.text.toString(),
            weight = weight,
            height = height,
            genre = binding.tvGenre.text.toString(),
            contacts = binding.contactEdt.unMasked,
            bloodType = binding.tvBloodType.text.toString(),
            category = binding.tvPlayerCategory.text.toString(),
            startDate = playerDetail.startDate

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
        val imageUrl = if (!playerDetail.images.isNullOrEmpty()) {
            playerDetail.images[0]
        } else {
            ""
        }

        if (imageUris.isNotEmpty()) {
            viewModel.updateImage(imageUrl, imageUris.first()) {
                when (it) {
                    is UiState.Loading -> {
                    }
                    is UiState.Failure -> {
                        toast(it.error)
                    }
                    is UiState.Success -> {
                        image.add(it.data.toString())
                        viewModel.updatePlayer(getPlayerObj())
                        // viewModel.deleteImage(playerDetail.images[0])
                        toast("suceso")
                        findNavController().popBackStack()
                    }
                }
            }
        } else {
            image.add(playerDetail.images[0])
            viewModel.updatePlayer(getPlayerObj())
            findNavController().popBackStack()
        }
    }

    companion object {
        const val PLAYER = "player"
    }


}