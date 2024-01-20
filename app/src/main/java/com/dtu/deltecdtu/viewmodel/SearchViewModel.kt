package com.dtu.deltecdtu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {
    private val _query = MutableLiveData<String>()
    val query: LiveData<String> get() =  _query

    fun setQuery(queryData: String) {
        _query.value=queryData
    }
}

