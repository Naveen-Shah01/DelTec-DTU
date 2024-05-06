package com.dtu.deltecdtu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dtu.deltecdtu.model.ModelFaculty
import com.dtu.deltecdtu.repository.MainRepository
import com.dtu.deltecdtu.util.Response

class FacultyViewModel : ViewModel() {
    private val repository = MainRepository()
    private val path = "/faculty"
    private val _facultyLiveData = MutableLiveData<Response<List<ModelFaculty>>>()
    val facultyLiveData: LiveData<Response<List<ModelFaculty>>> = _facultyLiveData
    init {
        repository.fetchFaculties(_facultyLiveData,path)
    }
}