package com.dtu.deltecdtu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtu.deltecdtu.Util.Response
import com.dtu.deltecdtu.model.ExtendedNoticeModel
import com.dtu.deltecdtu.repository.MainRepository


class LatestNewsViewModel() : ViewModel() {
    private val repository = MainRepository()
    private val _latestNewsLiveData = MutableLiveData<Response<List<ExtendedNoticeModel>>>()
    val latestNewsLiveData: LiveData<Response<List<ExtendedNoticeModel>>> = _latestNewsLiveData
    private val path = "/dtuNotices/latestNews/newsItem"
    fun fetchLatestNews() {
        repository.fetchDTUNotices(_latestNewsLiveData,path)
    }
}