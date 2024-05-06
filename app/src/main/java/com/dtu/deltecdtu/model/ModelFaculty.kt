package com.dtu.deltecdtu.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ModelFaculty(
    val facultyId:Int?,
    val categoryId: Int?,
    val designation: String?,
    val hierarchy: String?,
    val department: String?,
    val qualification: String?,
    val specialization: String?,
    val email: String?,
    val phone: Long?,
    val alternatePhone:Long?,
    val alternateEmail:String?,
    val name: String?,
    val profImageUrl:String?
) : Parcelable