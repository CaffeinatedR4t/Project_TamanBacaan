package com.caffeinatedr4t.tamanbacaan.data

import android.util.Log
import com.caffeinatedr4t.tamanbacaan.api.ApiConfig
import com.caffeinatedr4t.tamanbacaan.api.model.EventRequest
import com.caffeinatedr4t.tamanbacaan.api.model.EventResponse
import com.caffeinatedr4t.tamanbacaan.models.EventNotification
import com.caffeinatedr4t.tamanbacaan.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object EventRepository {

    private val events = mutableListOf<EventNotification>()

    fun getEvents(): List<EventNotification> = events

    fun loadEvents(onFinished: () -> Unit = {}) {
        ApiConfig.getApiService().getEvents()
            .enqueue(object : Callback<List<EventResponse>> {

                override fun onResponse(
                    call: Call<List<EventResponse>>,
                    response: Response<List<EventResponse>>
                ) {
                    if (response.isSuccessful) {
                        events.clear()
                        response.body()?.forEach {
                            events.add(
                                EventNotification(
                                    id = it._id,
                                    title = it.title,
                                    message = it.message,
                                    date = DateUtils.formatIsoToLocal(it.createdAt)
                                )
                            )
                        }
                    }
                    onFinished()
                }

                override fun onFailure(call: Call<List<EventResponse>>, t: Throwable) {
                    Log.e("EVENT_REPO", t.message.toString())
                    onFinished()
                }
            })
    }

    fun addEvent(
        token: String,
        title: String,
        message: String,
        onSuccess: () -> Unit = {}
    ) {
        val request = EventRequest(title, message)

        ApiConfig.getApiService()
            .addEvent("Bearer $token", request)
            .enqueue(object : Callback<EventResponse> {

                override fun onResponse(
                    call: Call<EventResponse>,
                    response: Response<EventResponse>
                ) {
                    if (response.isSuccessful) {
                        loadEvents(onSuccess)
                    }
                }

                override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                    Log.e("EVENT_REPO", t.message.toString())
                }
            })
    }

    fun deleteEvent(
        token: String,
        eventId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        ApiConfig.getApiService().deleteEvent("Bearer $token", eventId)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        // Reload data setelah berhasil hapus
                        loadEvents(onSuccess)
                    } else {
                        onError("Gagal menghapus: Kode ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.e("EVENT_REPO", t.message.toString())
                    onError(t.message ?: "Terjadi kesalahan")
                }
            })
    }
}
