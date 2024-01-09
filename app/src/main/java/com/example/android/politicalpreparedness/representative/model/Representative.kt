package com.example.android.politicalpreparedness.representative.model

import androidx.room.Embedded
import androidx.room.Entity
import com.example.android.politicalpreparedness.network.models.Office
import com.example.android.politicalpreparedness.network.models.Official

@Entity(tableName = "representative_table")
data class Representative (
        @Embedded (prefix = "official_") val official: Official,
        @Embedded (prefix = "office_") val office: Office
)