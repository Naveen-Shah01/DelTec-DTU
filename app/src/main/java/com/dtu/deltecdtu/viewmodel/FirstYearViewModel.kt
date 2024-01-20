package com.dtu.deltecdtu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtu.deltecdtu.Util.Response
import com.dtu.deltecdtu.model.ExtendedNoticeModel
import com.dtu.deltecdtu.repository.MainRepository

class FirstYearViewModel : ViewModel() {
    private val repository = MainRepository()
    private val _firstYearLiveData = MutableLiveData<Response<List<ExtendedNoticeModel>>>()
    val firstYearLiveData: LiveData<Response<List<ExtendedNoticeModel>>> = _firstYearLiveData
    private val path = "/dtuNotices/firstYear/firstYearItem"

    fun fetchLatestNews() {
        repository.fetchDTUNotices(_firstYearLiveData, path)
    }
}

