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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentPlayerDetailBinding
import com.dionis.escolinhajdb.util.Extensions.toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerDetailFragment : Fragment() {

    val TAG: String = "NoteDetailFragment"
    private lateinit var binding: FragmentPlayerDetailBinding
    private val viewModel: PlayerViewModel by viewModels()
    var imageUris: MutableList<Uri> = arrayListOf()
    lateinit var imageUri: Uri
    private lateinit var playerDetail: Player

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
        setInfo()
        loadImage()
        setUpClicks()
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
            binding.playerWeightEdt.setText(it.weight.toString())
            binding.playerHeightEdt.setText(it.height.toString())
            binding.responsibleNameEdt.setText(it.responsibleName)
            binding.responsibleTypeEdt.setText(it.responsibleType)
            binding.playerBirthEdt.setText(it.playersBirth)
            binding.healthNotesEdt.setText(it.healthNotes)
            binding.SkillsNotesEdt.setText(it.skillsNotes)
        }
        if (playerDetail.images.isNotEmpty()) {
            Picasso.get().load(playerDetail.images[0]).into(binding.playerImg)
        }
    }

    private fun setUpClicks() {
        binding.btnSave.setOnClickListener {
            //uploadImage()
            updatePlayer()
            findNavController().popBackStack()
        }

    }

    private fun getPlayerObj(): Player {

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
            weight = binding.playerWeightEdt.text.toString().toFloat(),
            height = binding.playerHeightEdt.text.toString().toFloat()
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