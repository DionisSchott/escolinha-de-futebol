package com.dionis.escolinhajdb.presentation.player

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
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentPlayerDetailBinding
import com.dionis.escolinhajdb.presentation.home.HomeActivity
import com.dionis.escolinhajdb.presentation.lists.ListViewModel
import com.dionis.escolinhajdb.presentation.pdf.FromPdfSaveFragment.Companion.KEY
import com.dionis.escolinhajdb.presentation.pdf.FromPdfSaveFragment.Companion.PLAYER_TO_PDF
import com.dionis.escolinhajdb.util.Extensions.copyToClipboard
import com.dionis.escolinhajdb.util.Extensions.datePicker
import com.dionis.escolinhajdb.util.Extensions.loadImage
import com.dionis.escolinhajdb.util.Extensions.setSpinner
import com.dionis.escolinhajdb.util.Extensions.toast
import com.dionis.escolinhajdb.util.Permissions
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
    var image: String = ""
    var imageUri: Uri? = null
    var age: Int = 0

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission(), ::handlePermissionResult)


    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            val fileUri = data?.data!!
            imageUris.add(fileUri)
            imageUri = fileUri
            Picasso.get().load(imageUri).into(binding.playerImg)

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
        setUpClicks()
        ageFormatter()
        setDatePicker()
        setInfo()
        setObservers()
        editActivating(false)
        (activity as HomeActivity).showBottomNavigation(false)
    }

    private fun setUpClicks() {

        binding.playerImg.setOnClickListener {
            tryLoadImage()
        }

        binding.btnEdit.setOnClickListener {
            turnVisible()
            editActivating(true)
            setUpSpinner()
        }

        binding.btnSave.setOnClickListener {
            uploadImage()
        }

        binding.exportPdf.setOnClickListener {
            tryExportPDF()
        }

        binding.savePdf.setOnClickListener {
            trySavePDF()
        }

        binding.tvContact.setOnLongClickListener {
            copyToClipboard(requireContext(),  binding.tvContact.unMasked)
        }
    }

    private fun setUpSpinner() {
        playerGenreSpinner()
        playerPositionSpinner()
        playerBloodSpinner()
        playerCategorySpinner()
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

    private fun savePDF() {
        val args = Bundle().apply {
            putParcelable(PLAYER_TO_PDF, playerDetail)
            putString(KEY, "save")
        }
        findNavController().navigate(R.id.action_playerDetailFragment_to_fromPdfSaveFragment, args)
    }

    private fun exportPDF() {
        val args = Bundle()
        args.putParcelable(PLAYER_TO_PDF, playerDetail)
        args.putString(KEY, "export")
        findNavController().navigate(R.id.action_playerDetailFragment_to_fromPdfSaveFragment, args)

    }

    private fun handlePermissionResult(isGranted: Boolean) {
        val permission = Permissions()

        val permissionState = permission.checkPermissionState(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        when (permissionState) {
            Permissions.PermissionState.GRANTED ->
                toast("permissão concedida!")

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

    private fun trySavePDF() {
        val permissions = Permissions()
        permissions.requestPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, { savePDF() }, { requestPermission() })

    }

    private fun tryExportPDF() {
        val permissions = Permissions()
        permissions.requestPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE, { exportPDF() }, { requestPermission() })
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

    private fun loadImage() {
        loadImage(startForProfileImageResult)
    }

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
        tvContact.isFocusable = isClickable
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
                Picasso.get().load(playerDetail.images).into(binding.playerImg)
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
            startDate = playerDetail.startDate,
        )
    }

    private fun uploadImage() {
        val imageUrl = if (playerDetail.images.isNotEmpty()) {
            playerDetail.images

        } else {
            ""
        }


        if (imageUri != null) {
            viewModel.updateImage(imageUrl, imageUris.first()) {
                when (it) {
                    is UiState.Loading -> {
                    }
                    is UiState.Failure -> {
                        toast(it.error)
                    }
                    is UiState.Success -> {
                        image = it.data.toString()
                        viewModel.updatePlayer(getPlayerObj())
                        toast("suceso")
                        findNavController().popBackStack()
                    }
                }
            }
        } else {
            image = playerDetail.images
            viewModel.updatePlayer(getPlayerObj())
            findNavController().popBackStack()
        }
    }

    companion object {
        const val PLAYER = "player"
    }


}