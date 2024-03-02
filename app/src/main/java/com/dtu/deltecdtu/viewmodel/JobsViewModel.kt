package com.dtu.deltecdtu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtu.deltecdtu.util.Response
import com.dtu.deltecdtu.model.ExtendedNoticeModel
import com.dtu.deltecdtu.repository.MainRepository

class JobsViewModel : ViewModel() {
    private val repository = MainRepository()
    private val _jobsLiveData = MutableLiveData<Response<List<ExtendedNoticeModel>>>()
    val jobsLiveData: LiveData<Response<List<ExtendedNoticeModel>>> = _jobsLiveData
    private val path = "/dtuNotices/jobs/jobItem"

    fun fetchLatestNews() {
        repository.fetchDTUNotices(_jobsLiveData, path)
    }
}

