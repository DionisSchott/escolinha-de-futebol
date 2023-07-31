package com.dionis.escolinhajdb.presentation.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dionis.escolinhajdb.States
import com.dionis.escolinhajdb.UiState
import com.dionis.escolinhajdb.data.model.Events
import com.dionis.escolinhajdb.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    val eventsRepository: EventRepository,
) : ViewModel() {


    private val _events = MutableLiveData<UiState<List<Events>>>()
    val events: LiveData<UiState<List<Events>>> get() = _events

    private val _updateEvents = MutableLiveData<UiState<String>>()
    val updateEvent: LiveData<UiState<String>> get() = _updateEvents

    private var _registerEvent = MutableLiveData<UiState<Pair<Events, String>>>()
    val registerEvent: LiveData<UiState<Pair<Events, String>>> get() = _registerEvent

    private val _validateFields: MutableLiveData<States.ValidateRegisterEvent> = MutableLiveData()
    val validateFields: LiveData<States.ValidateRegisterEvent> get() = _validateFields


    fun getEvents() {
        _events.value = UiState.Loading
        eventsRepository.getEvents { _events.value = it }
    }

    fun updateLists(event: Events) {
        _updateEvents.value = UiState.Loading
        eventsRepository.updateEvent(event) { _updateEvents.value = it }
    }

    fun createEvent(event: Events) {
        _registerEvent.value = UiState.Loading
        eventsRepository.createEvent(event) { _registerEvent.value = it }
    }

    fun validateFields(
        eventTitle: String,
        eventDescription: String,
        eventType: String,
    ) {
        if (validateAllFields(
                eventTitle,
                eventDescription,
                eventType
            )
        )
            _validateFields.value = States.ValidateRegisterEvent.FieldsDone
    }

    private fun validateAllFields(
        eventTitle: String,
        eventDescription: String,
        eventType: String,
    ): Boolean {

        if (eventTitle.isEmpty()) {
            _validateFields.value = States.ValidateRegisterEvent.EventTitleEmpty
            return false
        }
        if (eventDescription.isEmpty()) {
            _validateFields.value = States.ValidateRegisterEvent.EventDescriptionEmpty
            return false
        }
        if (eventType.isEmpty()) {
            _validateFields.value = States.ValidateRegisterEvent.EventTypeEmpty
            return false
        }

        return true

    }


}