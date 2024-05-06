package com.dtu.deltecdtu.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.dtu.deltecdtu.util.Response
import com.dtu.deltecdtu.util.Utility
import com.dtu.deltecdtu.model.ExtendedNoticeModel
import com.dtu.deltecdtu.model.FirebaseDTUNewsModel
import com.dtu.deltecdtu.model.ModelFaculty
import com.dtu.deltecdtu.model.ModelHoliday
import com.dtu.deltecdtu.model.NoticeModel
import com.dtu.deltecdtu.model.SubList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainRepository {
    private val database = Firebase.database
    fun fetchDTUNotices(
        latestNewsLiveData: MutableLiveData<Response<List<ExtendedNoticeModel>>>, path: String
    ) {
        val latestNewsReference = database.getReference(path)
        latestNewsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val latestNewsItem = mutableListOf<ExtendedNoticeModel>()
                for (ds in snapshot.children) {
                    val id = ds.child("id").getValue(Long::class.java)

                    val notice = ds.child("notice")
                    val name = notice.child("name").getValue(String::class.java)
                    println(name)
                    val link = notice.child("link").getValue(String::class.java)
                    val subList = notice.child("sub_list")
                    val subIListItems = mutableListOf<SubList>()
                    for (subItemSnapshot in subList.children) {
                        val subItemName = subItemSnapshot.child("name").getValue(String::class.java)
                        val subItemLink = subItemSnapshot.child("link").getValue(String::class.java)
                        val item = SubList(link = subItemLink, name = subItemName)
                        subIListItems.add(item)
                    }

                    val firebaseDTUNewsModel =
                        FirebaseDTUNewsModel(link = link, name = name, subList = subIListItems)
                    val noticeModel = NoticeModel(id = id, notice = firebaseDTUNewsModel)

                    val url = noticeModel.notice?.link ?: ""
                    val type = Utility.getFileTypeFromUrl(url)
                    val extendedNoticeModel = ExtendedNoticeModel(
                        noticeModel = noticeModel,
                        isDownloaded = false,
                        type = type,
                        bitmap = null,
                        fileName = null
                    )
                    latestNewsItem.add(extendedNoticeModel)
                }
                latestNewsItem.reverse()
                latestNewsLiveData.postValue(Response.Success(latestNewsItem))
            }

            override fun onCancelled(error: DatabaseError) {
                latestNewsLiveData.postValue(Response.Error("Some error occurred"))
            }
        })
    }

    fun fetchHolidays(
        holidaysLiveData2024: MutableLiveData<Response<List<ModelHoliday>>>,
        holidaysLiveData2023: MutableLiveData<Response<List<ModelHoliday>>>,
        path: String
    ) {
        val holidayReference = database.getReference(path)
        holidayReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val holidayList2024 = mutableListOf<ModelHoliday>()
                val holidayList2023 = mutableListOf<ModelHoliday>()
                for (ds in snapshot.children) {
                    val holiday = ds.getValue(ModelHoliday::class.java)
                    if (holiday != null) {
                        if (holiday.year == 2024) {
                            holidayList2024.add(holiday)
                        } else {
                            holidayList2023.add(holiday)
                        }
                    }
                }
                holidaysLiveData2024.postValue(Response.Success(holidayList2024))
                holidaysLiveData2023.postValue(Response.Success(holidayList2023))
            }

            override fun onCancelled(error: DatabaseError) {
                holidaysLiveData2024.postValue(Response.Error("$error"))
                holidaysLiveData2023.postValue(Response.Error("$error"))
            }
        })
    }

    fun fetchFaculties(
        facultyLiveData: MutableLiveData<Response<List<ModelFaculty>>>, path: String
    ) {
        val facultyReference = database.getReference(path)
        facultyReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val facultyList = mutableListOf<ModelFaculty>()
                for (ds in snapshot.children) {
                    val key = ds.key?.toInt()
                    val categoryId = ds.child("categoryId").getValue(Int::class.java)
                    val name = ds.child("name").getValue(String::class.java)
                    val designation = ds.child("designation").getValue(String::class.java)
                    // Check if the fields exist in the database snapshot before assigning
                    val specialization =
                        if (ds.hasChild("specialization")) ds.child("specialization")
                            .getValue(String::class.java) else null
                    val hierarchy = if (ds.hasChild("hierarchy")) ds.child("hierarchy")
                        .getValue(String::class.java) else null
                    val department = if (ds.hasChild("department")) ds.child("department")
                        .getValue(String::class.java) else null
                    val qualification = if (ds.hasChild("qualification")) ds.child("qualification")
                        .getValue(String::class.java) else null
                    val email = if (ds.hasChild("email")) ds.child("email")
                        .getValue(String::class.java) else null
                    val phone = if (ds.hasChild("phone")) ds.child("phone")
                        .getValue(Long::class.java) else null
                    val alternatePhone =
                        if (ds.hasChild("alternatePhone")) ds.child("alternatePhone")
                            .getValue(Long::class.java) else null
                    val alternateEmail =
                        if (ds.hasChild("alternateEmail")) ds.child("alternateEmail")
                            .getValue(String::class.java) else null
                    val profImageUrl = if (ds.hasChild("profImageUrl")) ds.child("profImageUrl")
                        .getValue(String::class.java) else null

                    val faculty = ModelFaculty(
                        key,
                        categoryId,
                        designation,
                        hierarchy,
                        department,
                        qualification,
                        specialization,
                        email,
                        phone,
                        alternatePhone,
                        alternateEmail,
                        name,
                        profImageUrl
                    )
                    facultyList.add(faculty)
                }
                facultyLiveData.postValue(Response.Success(facultyList))
            }

            override fun onCancelled(error: DatabaseError) {
                facultyLiveData.postValue(Response.Error("$error"))
            }
        })
    }
}