package com.mkitsimple.counterboredom.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Pokemon(val id: Int, val name: String): Parcelable {
    constructor() : this(1, "")
}