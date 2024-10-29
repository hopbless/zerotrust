package com.hopeinyang.zeroknowledge.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "searchHistory")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true)
    val primaryKey: Int = 0,
    val searchText:String = "",
    val searchDate:String = ""
)
