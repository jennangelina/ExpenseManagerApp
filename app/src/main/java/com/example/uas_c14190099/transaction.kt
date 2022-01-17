package com.example.uas_c14190099

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.DayOfWeek

@Parcelize
data class transaction(
    var id: String,
    var type: String,
    var amount: String,
    var category: String,
    var note: String,
    var day: String,
    var month: String,
    var year: String
): Parcelable
