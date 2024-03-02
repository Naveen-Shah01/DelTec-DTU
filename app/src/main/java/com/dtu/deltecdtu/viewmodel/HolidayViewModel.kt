package com.dtu.deltecdtu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtu.deltecdtu.model.ModelHoliday
import com.dtu.deltecdtu.repository.MainRepository
import com.dtu.deltecdtu.util.Response


//TODO use view-model factory
class HolidayViewModel : ViewModel() {
    private val repository = MainRepository()

    private val path = "/listOfHolidays"

    private val _holidaysLiveData2024 = MutableLiveData<Response<List<ModelHoliday>>>()
    val holidaysLiveData2024: LiveData<Response<List<ModelHoliday>>> = _holidaysLiveData2024

    private val _holidaysLiveData2023 = MutableLiveData<Response<List<ModelHoliday>>>()
    val holidaysLiveData2023: LiveData<Response<List<ModelHoliday>>> = _holidaysLiveData2023

    private val _query = MutableLiveData<String>()
    val query: LiveData<String> get() = _query
    fun setQuery(queryData: String) {
        _query.value = queryData
    }

    init {
        repository.fetchHolidays(_holidaysLiveData2024, _holidaysLiveData2023,path)
    }


}