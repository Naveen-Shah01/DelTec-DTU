package com.dtu.deltecdtu.model

import android.graphics.Bitmap
import java.io.Serializable
import java.util.UUID

//8.
data class SubListModel(
    var name: String? = null,
    val url: String? = null,
    var isDownloaded: Boolean = false,
    var type: String = "",
    var bitmap: Bitmap? = null,
    var fileName:String?=null
):Serializable
