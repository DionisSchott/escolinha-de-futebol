package com.dionis.escolinhajdb.presentation.pdf

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.databinding.FragmentFromPdfSaveBinding
import com.dionis.escolinhajdb.util.Extensions.toast
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FromPdfSaveFragment : Fragment() {

    private lateinit var binding: FragmentFromPdfSaveBinding
    private lateinit var exportPdf: ExportPdf
    private lateinit var playerDetail: Player
    private lateinit var keyForAction: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFromPdfSaveBinding.inflate(inflater, container, false)

        playerDetail = arguments?.getParcelable(PLAYERTOPDF)!!
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
        Picasso.get().load(playerDetail.images[0]).into(binding.playerImg)
        binding.playerNameEdt.text = playerDetail.playerName
        binding.tvPlayerPosition.text = playerDetail.position
        binding.playerWeightEdt.text = playerDetail.weight.toString()
        binding.playerHeightEdt.text = playerDetail.height.toString()
        binding.tvGenre.text = playerDetail.genre
        binding.responsibleNameEdt.text = playerDetail.responsibleName
        binding.responsibleTypeEdt.text = playerDetail.responsibleType
        binding.playerBirthEdt.text = playerDetail.playersBirth
        binding.playerAgeTv.text = "10"

//        val currentDay = Calendar.getInstance()
//        currentDay.time
//        val age = (currentDay.get(Calendar.YEAR) - playerDetail.playersBirth

        if (playerDetail.healthNotes.isNotEmpty()) {
            binding.healthNotesEdt.setText(playerDetail.healthNotes)
        } else {
            binding.healthNotesEdt.visibility = View.INVISIBLE
            binding.tvHealthNotes.visibility = View.INVISIBLE
        }

        if (playerDetail.skillsNotes.isNotEmpty()) {
            binding.skillsNotesEdt.setText(playerDetail.skillsNotes)
        } else {
            binding.skillsNotesEdt.visibility = View.INVISIBLE
            binding.tvSkillsNotes.visibility = View.INVISIBLE
        }
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

//    private fun permissao() {
//
//        val permissionReadExternalStorage = ActivityCompat.checkSelfPermission(requireContext(),
//            Manifest.permission.READ_EXTERNAL_STORAGE)
//        val permissionWriteExternalStorage = ActivityCompat.checkSelfPermission(requireContext(),
//            Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        if (permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED ||
//            permissionWriteExternalStorage != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(requireActivity(),
//                arrayOf(
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
//                REQUEST_PERMISSION_CODE)
//            return
//        }
//    }

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
        const val PLAYERTOPDF = "playerToPdf"
        const val KEY = "key"
        const val REQUEST_PERMISSION_CODE = 57


    }
}