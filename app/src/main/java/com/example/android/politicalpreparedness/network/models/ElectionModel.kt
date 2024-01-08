package com.example.android.politicalpreparedness.network.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.*

@Keep
@Parcelize
data class ElectionModel(
        val id: Int,
        val name: String,
        val electionDay: Date,
        val division: Division,
        val saved: Boolean
) : Parcelable

fun ElectionModel.toDataModel() =
        Election(
                id = id,
                name = name,
                electionDay = electionDay,
                division = division,
                saved = saved
        )
