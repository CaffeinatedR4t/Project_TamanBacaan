package com.caffeinatedr4t.tamanbacaan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.caffeinatedr4t.tamanbacaan.data.EventRepository
import com.caffeinatedr4t.tamanbacaan.models.EventNotification

class EventViewModel : ViewModel() {

    private val _events = MutableLiveData<List<EventNotification>>()
    val events: LiveData<List<EventNotification>> = _events

    fun loadEvents() {
        EventRepository.loadEvents {
            _events.postValue(EventRepository.getEvents())
        }
    }

    fun addEvent(
        token: String,
        title: String,
        message: String
    ) {
        EventRepository.addEvent(token, title, message) {
            _events.postValue(EventRepository.getEvents())
        }
    }

    fun notifyNewBookAdded(
        token: String,
        bookTitle: String
    ) {
        val title = "📚 Buku Baru Ditambahkan"
        val message = "Buku \"$bookTitle\" telah tersedia di perpustakaan."

        EventRepository.addEvent(token, title, message) {
            _events.postValue(EventRepository.getEvents())
        }
    }
}
