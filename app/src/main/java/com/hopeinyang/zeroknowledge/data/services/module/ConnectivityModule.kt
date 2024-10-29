package com.hopeinyang.zeroknowledge.data.services.module

import android.content.Context
import com.hopeinyang.zeroknowledge.data.dao.ConnectivityObserver
import com.hopeinyang.zeroknowledge.data.dao.impl.ConnectivityObserverImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ConnectivityModule {

    @Provides
    fun provideInternetConnectivityImpl(@ApplicationContext appContext: Context) : ConnectivityObserver {
        return ConnectivityObserverImpl(appContext)
    }
}