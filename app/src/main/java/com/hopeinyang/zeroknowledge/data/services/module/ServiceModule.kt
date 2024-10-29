package com.hopeinyang.zeroknowledge.data.services.module

import com.hopeinyang.zeroknowledge.data.dao.AccountService
import com.hopeinyang.zeroknowledge.data.dao.LogService
import com.hopeinyang.zeroknowledge.data.dao.StorageService
import com.hopeinyang.zeroknowledge.data.dao.impl.AccountServiceImpl
import com.hopeinyang.zeroknowledge.data.dao.impl.LogServiceImpl
import com.hopeinyang.zeroknowledge.data.dao.impl.StorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {


    @Binds
    abstract fun provideLogService(impl: LogServiceImpl): LogService

    @Binds
    abstract fun provideAccountService(impl: AccountServiceImpl):AccountService

    @Binds
    abstract fun provideStorageService(impl:StorageServiceImpl):StorageService






    //@Binds abstract fun provideConfigurationService(impl: ConfigurationServiceImpl): ConfigurationService

}