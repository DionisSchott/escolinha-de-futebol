package com.dionis.escolinhajdb.presentation.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.databinding.FragmentUpdateUserInfoBinding
import com.dionis.escolinhajdb.presentation.coach.DialogCoachDetailFragment
import com.dionis.escolinhajdb.util.Extensions.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateUserInfoFragment : Fragment() {

    private lateinit var binding: FragmentUpdateUserInfoBinding
    private lateinit var coach: Coach
    private val viewModel: ViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUpdateUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coach = arguments?.getParcelable(COACH)!!
        setData()
        setUpClicks()
    }

    private fun setData() {
        binding.edtUserName.setText(coach.name)
        binding.edtCategory.setText(coach.category)
    }

    private fun getCoachObject(): Coach {

        return Coach(
            id = coach.id,
            name = binding.edtUserName.text.toString(),
            email = coach.email,
            photo = coach.photo,
            function = coach.function,
            category = coach.category,
            birth = coach.birth,
            genre = coach.genre,
            contact = coach.contact,
            memberSince = coach.memberSince
        )
    }

    private fun setUpClicks() {
        binding.btnDone.setOnClickListener {
            observer()
            updateUserInfo()
        }
    }

    private fun updateUserInfo() {
        viewModel.updateUserInfo(getCoachObject())
    }


    private fun observer() {

        viewModel.updateInfo.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                }
                is UiState.Failure -> {
                    toast(it.error)
                }
                is UiState.Success -> {
                    toast("suceso")
                    findNavController().popBackStack()
                }


            }

        }

    }

    companion object {
        const val COACH = "coach"
    }

}