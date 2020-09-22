package com.example.mapsfirebase.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MapsUserRegister (
    var register_nama: String? = null,
    var register_nama2: String? = null,
    var register_lat: String? = null,
    var register_lon: String? = null
): Parcelable {
    constructor(): this("", "", "", "")
}