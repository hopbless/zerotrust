package com.hopeinyang.zeroknowledge.data.dao.offline

import com.hopeinyang.zeroknowledge.data.dto.SearchHistory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ZeroTrustDBRepositoryImpl @Inject constructor(
    private val itemDAO: ZeroTrustDAO
):ZeroTrustRepositoryService {


    override fun getSearchHistory(): Flow<List<SearchHistory>> = itemDAO.getSearchHistory()
    override suspend fun insertSearchHistory(searchHistory: SearchHistory) = itemDAO.insertSearchHistory(searchHistory)


}