package com.dtu.deltecdtu.repository

import androidx.lifecycle.MutableLiveData
import com.dtu.deltecdtu.util.Response
import com.dtu.deltecdtu.util.Utility
import com.dtu.deltecdtu.model.ExtendedNoticeModel
import com.dtu.deltecdtu.model.FirebaseDTUNewsModel
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
}