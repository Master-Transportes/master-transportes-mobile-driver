package com.master.transportes.driver.di.lifecycle

import androidx.lifecycle.ProcessLifecycleOwner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LifecycleModule {

    @Provides
    @Singleton
    fun provideProcessLifecycleOwner(): ProcessLifecycleOwner {
        return ProcessLifecycleOwner.get() as ProcessLifecycleOwner
    }
}