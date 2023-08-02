package com.dionis.escolinhajdb.presentation.events

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dionis.escolinhajdb.R
import com.dionis.escolinhajdb.States
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Events
import com.dionis.escolinhajdb.databinding.FragmentRegisterEventBinding
import com.dionis.escolinhajdb.presentation.home.HomeActivity
import com.dionis.escolinhajdb.util.Extensions.convertLocalDateToDate
import com.dionis.escolinhajdb.util.Extensions.datePickerReturn
import com.dionis.escolinhajdb.util.Extensions.showObligatoryField
import com.dionis.escolinhajdb.util.Extensions.spinnerAutoComplete
import com.dionis.escolinhajdb.util.Extensions.toast
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.reflect.typeOf

@AndroidEntryPoint
class EventRegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterEventBinding
    private val viewModel: EventsViewModel by viewModels()
    var objEvent: Events? = null
    private var eventList = listOf<String>("Jogo", "Confraternização", "Reunião" )

    //    var imageUris: MutableList<Uri> = arrayListOf()
//    var image: String = ""
//    var imageUri: Uri? = null
    var date = Calendar.getInstance().time


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentRegisterEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as HomeActivity).showBottomNavigation(true)
    }

    private fun setUp() {
        getEventDate()
        setObserver()
        setupClicks()
        spinnerEvent()


        (activity as HomeActivity).showBottomNavigation(false)
    }

    private fun spinnerEvent() {
        spinnerAutoComplete(binding.edtEventType, eventList)
    }

    private fun setupClicks() {
        binding.btnDone.setOnClickListener {
            validateFields()
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun setObserver() {

        viewModel.validateFields.observe(viewLifecycleOwner) {
            when (it) {
                is States.ValidateRegisterEvent.EventTitleEmpty -> {
                    showObligatoryField(binding.eventNameLayout, R.string.obligatory_field)
                }
                is States.ValidateRegisterEvent.EventDescriptionEmpty -> {
                    showObligatoryField(binding.eventDescriptionLayout, R.string.obligatory_field)
                }
                is States.ValidateRegisterEvent.EventTypeEmpty -> {
                    showObligatoryField(binding.eventTypeLayout, R.string.obligatory_field)
                }
                is States.ValidateRegisterEvent.FieldsDone -> {
                    registerEvent()
                }
            }

        }


        viewModel.registerEvent.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.load.visibility = View.VISIBLE
                }
                is UiState.Failure -> {
                    binding.load.visibility = View.INVISIBLE
                    toast(it.error)
                }
                is UiState.Success -> {
                    binding.load.visibility = View.INVISIBLE
                    objEvent = it.data.first
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun validateFields() {
        viewModel.validateFields(
            binding.eventName.text.toString(),
            binding.edtEventDescription.text.toString(),
            binding.edtEventType.text.toString()
        )
    }

    private fun getEventDate() {

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.edtEventDate.setText(formatter.format(date))

        datePickerReturn("", binding.edtEventDate) {
            date = convertLocalDateToDate(it)
            binding.edtEventDate.setText(formatter.format(date))
        }

    }

    private fun getEventObj(): Events {

        val coach = FirebaseAuth.getInstance().currentUser?.uid

        return Events(
            id = "",
            title = binding.eventName.text.toString(),
            description = binding.edtEventDescription.text.toString(),
            addedBy = coach!!,
            date = date,
            eventType = binding.edtEventType.text.toString()

        )
    }

    private fun registerEvent() {
        viewModel.createEvent(
            event = getEventObj()
        )
    }

}