package com.example.mapsfirebase.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Maps (
    var nama: String? = null,
    var nama2: String? = null,
    var lat: String? = null,
    var lon: String? = null,
    var key: String? = null
): Parcelable {
    constructor(): this("", "", "", "", "")
}