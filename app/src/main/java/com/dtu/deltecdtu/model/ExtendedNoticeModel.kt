package com.dtu.deltecdtu.model

import android.graphics.Bitmap
import java.io.Serializable

data class ExtendedNoticeModel(
    var noticeModel: NoticeModel,
    var isDownloaded: Boolean = false,
    var type: String="",
    var bitmap: Bitmap? = null,
    var fileName:String?=null
) : Serializable