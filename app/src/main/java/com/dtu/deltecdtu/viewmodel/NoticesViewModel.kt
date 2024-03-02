package com.dtu.deltecdtu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtu.deltecdtu.util.Response
import com.dtu.deltecdtu.model.ExtendedNoticeModel
import com.dtu.deltecdtu.repository.MainRepository

class NoticesViewModel : ViewModel() {
    private val repository = MainRepository()
    private val _latestNoticesLiveData = MutableLiveData<Response<List<ExtendedNoticeModel>>>()
    val latestNoticesLiveData: LiveData<Response<List<ExtendedNoticeModel>>> = _latestNoticesLiveData

    private val path = "/dtuNotices/notices/noticeItem"
    fun fetchLatestNews(){
        repository.fetchDTUNotices(_latestNoticesLiveData, path)
    }
}