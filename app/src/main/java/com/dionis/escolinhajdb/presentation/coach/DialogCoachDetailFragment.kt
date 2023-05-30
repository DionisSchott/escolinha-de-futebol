package com.dionis.escolinhajdb.presentation.coach

import android.graphics.Color
import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.icu.util.ULocale
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.databinding.FragmentCoachDetailsBinding
import com.dionis.escolinhajdb.presentation.auth.ViewModel
import com.squareup.picasso.Picasso
import java.time.format.DateTimeFormatter

class DialogCoachDetailFragment() : DialogFragment() {

    private lateinit var binding: FragmentCoachDetailsBinding
    private lateinit var coach: Coach
    private val viewModel: ViewModel by viewModels()
    var age: Int = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentCoachDetailsBinding.inflate(inflater, container, false)

        coach = arguments?.getParcelable(COACH)!!
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setup()
    }

    private fun setup() {

        binding.imgExit.setOnClickListener { dismiss() }
        binding.imgCoach.setOnClickListener { findNavController().navigate(R.id.registerPlayerFragment) }
        setData()
    }


    private fun setData() = binding.apply {
        coach.let {
            if (it.photo.isNotEmpty()) {
                Picasso.get().load(it.photo).into(imgCoach)
            }
            tvCoachName.setText(it.name)
            tvCoachFunction.text = it.function
            tvCoachSubFunction.text = it.subFunction

//            val format = DecimalFormat("(##)#####-####")
//            val formatContact = format.format(it.contact.toLong())

            if (it.contact.isNotEmpty()) {
                val areaCode = it.contact.substring(0, 2)
                val prefix = it.contact.substring(2, 7)
                val line = it.contact.substring(7, 11)
                val formatted = String.format("(%s) %s-%s", areaCode, prefix, line)
                tvContact.text = formatted
            }

            val outputFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val formattedDate = coach.birth.format(outputFormat) // Formatar a data no padrão "dd-MM-yyyy"
            tvMemberSince.text = formattedDate

        }
    }

    companion object {
        const val COACH = "coach"
    }

}