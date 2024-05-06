package com.dtu.deltecdtu

import android.widget.TextView
import com.dtu.deltecdtu.adapter.DTUNoticeAdapter
import com.dtu.deltecdtu.model.ModelFaculty
import com.dtu.deltecdtu.model.ModelHoliday
import com.dtu.deltecdtu.model.NoticeModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView

interface Downloader {
    fun downloadFileFromDTUNoticeAdapter(
        url: String, position: Int, dtuNoticeAdapter: DTUNoticeAdapter
    ): Long
}

interface NoticeClickListener {
    fun onReadMoreClick(position: Int, model: NoticeModel)
    fun onThumbnailItemClick(url: String)
    fun linkLongClick(url: String, tvLink: TextView)
    fun linkClick(url: String)
    fun onDownloadClick(position: Int, url: String, cdDownload: MaterialCardView)
}

interface SubListNoticeClickListener {
    fun onThumbnailItemClick(url: String)
    fun linkLongClick(url: String, tvLink: TextView)
    fun linkClick(url: String)
    fun onDownloadClick(position: Int, url: String, cdDownload: MaterialCardView)
}

interface OpenDrawer {
    fun openDrawer()
}

interface HolidayItemClickListener {
    fun onHolidayClicked(modelHoliday: ModelHoliday)
}

interface FacultyItemClickListener {
    fun onFacultyClicked(modelFaculty: ModelFaculty)
    fun onFacultyEmailPopDialogClicked(email:String?,alternateEmail:String?)
}

