package com.example.bigjourney.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Trip(
    @PrimaryKey(autoGenerate = true) val tid: Int? = null,
    @ColumnInfo(name = "location") val location: String?,
    @ColumnInfo(name = "start_date") val startDate: Date,
    @ColumnInfo(name = "end_date") val endDate: Date,
)
