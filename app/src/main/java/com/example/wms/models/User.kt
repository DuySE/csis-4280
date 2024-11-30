package com.example.wms.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var username: String,
    var password: String,
    var isAdmin: Boolean = false, // Default role is regular user
    var address: String? = null,
    var phone: String? = null,
    var profileImg: String? = null
) : Parcelable

@Parcelize
data class LoginRequest(
    val username: String,
    val password: String
) : Parcelable