package com.dionis.escolinhajdb.presentation.pdf

import android.Manifest
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentFromPdfSaveBinding
import com.dionis.escolinhajdb.util.Extensions.formatDate
import com.dionis.escolinhajdb.util.Extensions.toast
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date


class FromPdfSaveFragment : Fragment() {

    private lateinit var binding: FragmentFromPdfSaveBinding
    private lateinit var exportPdf: ExportPdf
    private lateinit var playerDetail: Player
    private lateinit var keyForAction: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFromPdfSaveBinding.inflate(inflater, container, false)

        playerDetail = arguments?.getParcelable(PLAYER_TO_PDF)!!
        keyForAction = arguments?.getString(KEY)!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        exportPdf = ExportPdf()
        setInfo()
        click()

        viewLifecycleOwner.lifecycleScope.launch {
            delay(50)
            if (keyForAction == "save") {
                savePdf(view)
            } else if (keyForAction == "export") {
                exportPdf(view)
            }
        }
    }


    private fun setInfo() {
        Picasso.get().load(playerDetail.images).into(binding.playerImg)
        binding.playerNameEdt.text = playerDetail.playerName
        binding.tvMemberSince.text = playerDetail.startDate.toString()
        binding.playerWeightEdt.text = playerDetail.weight.toString().plus(" Kg")
        binding.playerHeightEdt.text = playerDetail.height.toString().plus(" M")
        binding.tvGenre.text = playerDetail.genre
        binding.responsibleNameEdt.text = playerDetail.responsibleName.plus(" (${playerDetail.responsibleType})")
        binding.playerBirthEdt.text = playerDetail.playersBirth
        binding.playerCategory.text = getString(R.string.current_category).plus("  ${playerDetail.category}")
        binding.tvBloodType.text = playerDetail.bloodType

        val formattedDate = formatDate(playerDetail.startDate!!)
        binding.tvMemberSince.text = formattedDate

        binding.healthNotesEdt.setText(playerDetail.healthNotes)

        if (playerDetail.skillsNotes.isNotEmpty()) {
            binding.skillsNotesEdt.setText(playerDetail.skillsNotes)
        } else {
            binding.tvSkillsNotes.visibility = View.INVISIBLE
        }

        val date = Calendar.getInstance().time
        binding.date.text = getString(R.string.sao_jose).plus("  -  ${formatDate(date)}")
    }


    private fun exportPdf(layout: View) {

        exportPdf.sharePdf(requireContext(), layout, playerDetail.playerName + playerDetail.category)
        findNavController().popBackStack()

    }

    private fun savePdf(layout: View) {

        exportPdf.savePdf(layout, playerDetail.playerName + playerDetail.category)
        toast("salvo com sucesso")
        findNavController().popBackStack()

    }


    private fun click() {

        binding.bottomLayout.setOnClickListener { onSaveClick() }
        binding.playerImg.setOnClickListener { onExportClick() }
    }

    private fun onExportClick() {
        if (!isPermissionGranted()) {
            requestPermission()
        } else {
            exportPdf(binding.layout)
        }
    }

    private fun onSaveClick() {

        if (!isPermissionGranted()) {
            requestPermission()
        } else {
            savePdf(binding.layout)
        }

    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSION_CODE
        )
    }

    companion object {
        const val PLAYER_TO_PDF = "playerToPdf"
        const val KEY = "key"
        const val REQUEST_PERMISSION_CODE = 57


    }
}