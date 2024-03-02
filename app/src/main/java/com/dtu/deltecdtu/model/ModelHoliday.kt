package com.dtu.deltecdtu.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelHoliday(
    val dateId: Int? = null,
    val holiday: String = "",
    val date: Int? = null,
    val month: String = "",
    val sakaMonth: String = "",
    val sakaDate: Int? = null,
    val day: String = "",
    val year: Int? = null,
    val holidayImageLink: String = "",
    val holidayDescription: String = "",
    val backgroundColor:String=""
) : Parcelable