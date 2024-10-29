package com.hopeinyang.zeroknowledge.data.services.module

import android.content.Context
import com.hopeinyang.zeroknowledge.data.dao.offline.ZeroTrustDAO
import com.hopeinyang.zeroknowledge.data.dao.offline.ZeroTrustDBRepositoryImpl
import com.hopeinyang.zeroknowledge.data.dao.offline.ZeroTrustDatabase
import com.hopeinyang.zeroknowledge.data.dao.offline.ZeroTrustRepositoryService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RoomDatabaseService {
    @Provides
    @ViewModelScoped
    fun provideZeroTrustDAO(@ApplicationContext appContext:Context):ZeroTrustDAO{
        return ZeroTrustDatabase.getInstance(appContext).zeroTrustDAO
    }

    @Provides
    @ViewModelScoped
    fun providesZeroTrustDBRepository(dao:ZeroTrustDAO): ZeroTrustRepositoryService {
        return ZeroTrustDBRepositoryImpl(dao)
    }
}