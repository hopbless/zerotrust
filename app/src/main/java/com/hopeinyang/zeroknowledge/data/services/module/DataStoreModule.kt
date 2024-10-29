package com.hopeinyang.zeroknowledge.data.services.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dao.UserPreferenceService
import com.hopeinyang.zeroknowledge.data.dao.impl.AccountServiceImpl
import com.hopeinyang.zeroknowledge.data.dao.impl.StorageServiceImpl
import com.hopeinyang.zeroknowledge.data.dao.impl.UserPreferenceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

import javax.inject.Singleton

const val USER_PREFERENCES = "zero_trust_preferences"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {

        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler (
                produceNewData = { emptyPreferences() }
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(USER_PREFERENCES) }
        )
    }




    @Provides
    fun provideUserPreferences(dataStore: DataStore<Preferences>): UserPreferenceService {
        return UserPreferenceRepositoryImpl(dataStore)
    }
}