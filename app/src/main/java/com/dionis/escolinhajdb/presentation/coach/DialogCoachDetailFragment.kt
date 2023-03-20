package com.dionis.escolinhajdb.presentation.coach

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.databinding.FragmentCoachDetailsBinding
import com.dionis.escolinhajdb.presentation.auth.ViewModel
import com.dionis.escolinhajdb.util.Extensions.navTo

class DialogCoachDetailFragment() : DialogFragment() {

    private lateinit var binding: FragmentCoachDetailsBinding
    private lateinit var coach: Coach
    private val viewModel: ViewModel by viewModels()

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

        setData()
        updateUserInfo()
    }

    private fun setData() {
        binding.imgExit.setOnLongClickListener { findNavController().popBackStack() }
        binding.tvCoachName.setText(coach.name)
        binding.tvCoachCategory.text = coach.category
    }


    private fun updateUserInfo() {
        binding.imgExit.setOnClickListener {
            viewModel.updateUserInfo(coach)
        }
    }


    companion object {
        const val COACH = "coach"
    }

}