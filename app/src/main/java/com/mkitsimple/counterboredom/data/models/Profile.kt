package com.mkitsimple.counterboredom.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Profile(val uid: String, val profileImageUrl: String, val username: String):
    Parcelable {
    constructor() : this("", "", "")
}