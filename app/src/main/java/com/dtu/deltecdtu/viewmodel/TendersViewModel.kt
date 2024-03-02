package com.dtu.deltecdtu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtu.deltecdtu.util.Response
import com.dtu.deltecdtu.model.ExtendedNoticeModel
import com.dtu.deltecdtu.repository.MainRepository

class TendersViewModel : ViewModel() {
    private val repository = MainRepository()
    private val _tendersLiveData = MutableLiveData<Response<List<ExtendedNoticeModel>>>()
    val tendersLiveData: LiveData<Response<List<ExtendedNoticeModel>>> = _tendersLiveData
    private val path = "/dtuNotices/tenders/tenderItem"

    fun fetchLatestNews() {
        repository.fetchDTUNotices(_tendersLiveData, path)
    }
}

