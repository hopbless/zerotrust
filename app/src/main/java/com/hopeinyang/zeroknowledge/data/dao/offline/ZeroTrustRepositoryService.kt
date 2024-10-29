package com.hopeinyang.zeroknowledge.data.dao.offline

import com.hopeinyang.zeroknowledge.data.dto.SearchHistory
import kotlinx.coroutines.flow.Flow

interface ZeroTrustRepositoryService {

    fun getSearchHistory(): Flow<List<SearchHistory>>
    suspend fun insertSearchHistory(searchHistory: SearchHistory)
}