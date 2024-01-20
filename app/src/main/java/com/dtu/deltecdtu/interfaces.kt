package com.dtu.deltecdtu

import android.widget.TextView
import com.dtu.deltecdtu.adapter.DTUNoticeAdapter
import com.dtu.deltecdtu.model.NoticeModel
import com.google.android.material.card.MaterialCardView

interface Downloader {
    fun downloadFileFromDTUNoticeAdapter(url: String, position: Int, dtuNoticeAdapter: DTUNoticeAdapter): Long
}
interface NoticeClickListener {
    fun onReadMoreClick(position: Int, model: NoticeModel)
    fun onThumbnailItemClick(url: String)
    fun linkLongClick(url: String, tvLink: TextView)
    fun linkClick(url:String)
    fun onDownloadClick(position: Int, url: String, cdDownload: MaterialCardView)
}
interface SubListNoticeClickListener  {
    fun onThumbnailItemClick(url: String)
    fun linkLongClick(url: String, tvLink: TextView)
    fun linkClick(url:String)
    fun onDownloadClick(position: Int, url: String, cdDownload: MaterialCardView)
}


