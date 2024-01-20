package com.dtu.deltecdtu.model

import java.io.Serializable

data class FirebaseDTUNewsModel(
    var link: String? = null,
    var name: String? = null,
    var subList: List<SubList>? = null
):Serializable
