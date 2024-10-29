package com.hopeinyang.zeroknowledge.data.services.module

import android.content.Context

import com.hopeinyang.zeroknowledge.data.dao.LocationService
import com.hopeinyang.zeroknowledge.data.dao.TrustScoreService

import com.hopeinyang.zeroknowledge.data.dao.impl.LocationServiceImpl
import com.hopeinyang.zeroknowledge.data.dao.impl.LocationUpdates
import com.hopeinyang.zeroknowledge.data.dao.impl.TrustScoreImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object TrustScoreModule {


    @Provides
    fun provideLocationImp(@ApplicationContext appContext: Context) : LocationService {
        return LocationUpdates(appContext)
    }

    @Provides
    fun provideTrustScoreImpl(@ApplicationContext appContext: Context):TrustScoreService{
        return TrustScoreImpl(appContext)
    }
}