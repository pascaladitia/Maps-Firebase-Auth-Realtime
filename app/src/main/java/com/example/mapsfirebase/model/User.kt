package com.example.mapsfirebase.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    var status: String? = null,
    var nama: String? = null,
    var email: String? = null,
    var hp: String? = null,
    var job: String? = null,
    var alamat: String? = null,
    var password: String? = null,
    var key: String? = null
): Parcelable
{
    constructor(): this("", "", "", "",
        "", "", "")
}