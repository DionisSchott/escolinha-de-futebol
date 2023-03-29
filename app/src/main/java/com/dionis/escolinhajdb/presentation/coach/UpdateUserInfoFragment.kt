package com.dionis.escolinhajdb.presentation.coach


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Coach
import com.dionis.escolinhajdb.databinding.FragmentUpdateUserInfoBinding
import com.dionis.escolinhajdb.presentation.auth.ViewModel
import com.dionis.escolinhajdb.util.Extensions.datePicker
import com.dionis.escolinhajdb.util.Extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class UpdateUserInfoFragment : Fragment() {

    private lateinit var binding: FragmentUpdateUserInfoBinding
    private lateinit var coach: Coach
    private val viewModel: ViewModel by viewModels()
    private val myCalendar = Calendar.getInstance()
    private val functionList = listOf("Presidente", "Treinador(a)", "Auxiliar")
    private var function = ""

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
        endIconClick()
        setDatePicker()
    }

    private fun setData() = binding.apply {
        edtUserName.setText(coach.name)
        mskContact.setText(coach.contact)
        tvFunction.setText(coach.function)
        edtSubFunction.setText(coach.subFunction)
        edtBirth.setText(coach.birth)
    }


    private fun getCoachObject(): Coach {

        return Coach(
            id = coach.id,
            name = binding.edtUserName.text.toString(),
            email = coach.email,
            photo = coach.photo,
            function = coach.function,
            subFunction = binding.edtSubFunction.text.toString(),
            birth = binding.edtBirth.text.toString(),
            genre = coach.genre,
            contact = binding.mskContact.unMasked,
            memberSince = coach.memberSince
        )
    }

    private fun setUpClicks() {
        binding.btnDone.setOnClickListener {
            observer()
            updateUserInfo()

        }
    }

    private fun endIconClick() {

        binding.edtUserNameLayout.setEndIconOnClickListener {
            binding.edtUserName.text?.clear()
            binding.edtUserName.requestFocus()
        }

        binding.edtFunctionLayout.setEndIconOnClickListener {
            coachFunctionSpinner()

        }

    }

    private fun updateUserInfo() {
        viewModel.updateUserInfo(getCoachObject())
    }


    private fun coachFunctionSpinner() {

        binding.edtFunctionLayout.visibility = View.INVISIBLE
        binding.cvFunctionSpinner.visibility = View.VISIBLE

        binding.edtFunctionSpinner.post { binding.edtFunctionSpinner.performClick() }

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            functionList.map { it })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.edtFunctionSpinner.adapter = adapter
        val spinnerPosition = adapter.getPosition(binding.tvFunction.text.toString())
        binding.edtFunctionSpinner.setSelection(spinnerPosition)

        binding.edtFunctionSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemview: View?,
                    position: Int,
                    id: Long,
                ) {
                    val selectedPlayerPosition = functionList[position]
                    function = selectedPlayerPosition
                    binding.tvFunction.setText(function)
                    binding.edtFunctionLayout.visibility = View.VISIBLE
                    binding.cvFunctionSpinner.visibility = View.INVISIBLE

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }


    private fun setDatePicker() {

        datePicker(coach.birth, binding.edtBirth)

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