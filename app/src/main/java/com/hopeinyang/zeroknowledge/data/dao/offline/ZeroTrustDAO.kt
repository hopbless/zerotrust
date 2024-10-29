package com.hopeinyang.zeroknowledge.data.dao.offline

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hopeinyang.zeroknowledge.data.dto.SearchHistory
import kotlinx.coroutines.flow.Flow
@Dao
interface ZeroTrustDAO {
    @Insert
    suspend fun insertSearchHistory(history:SearchHistory)

    @Query("SELECT * from searchHistory ORDER BY searchDate DESC")
    fun getSearchHistory(): Flow<List<SearchHistory>>
}