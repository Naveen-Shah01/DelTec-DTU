package com.dtu.deltecdtu.model

import java.io.Serializable

data class NoticeModel(
    var id: Long?=null,
    var notice: FirebaseDTUNewsModel? = null
):Serializable