package com.dtu.deltecdtu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtu.deltecdtu.util.Response
import com.dtu.deltecdtu.model.ExtendedNoticeModel
import com.dtu.deltecdtu.repository.MainRepository

class EventsViewModel : ViewModel() {
    private val repository = MainRepository()
    private val _latestEventsLiveData = MutableLiveData<Response<List<ExtendedNoticeModel>>>()
    val latestEventsLiveData: LiveData<Response<List<ExtendedNoticeModel>>> = _latestEventsLiveData
    private val path = "/dtuNotices/events/eventItem"
    fun fetchLatestNews() {
        repository.fetchDTUNotices(_latestEventsLiveData, path)
    }
}

