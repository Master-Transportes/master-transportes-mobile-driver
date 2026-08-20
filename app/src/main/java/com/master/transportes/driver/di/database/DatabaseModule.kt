package com.master.transportes.driver.di.database

import android.content.Context
import androidx.room.Room
import com.master.transportes.driver.core.database.MasterTransportesDatabase
import com.master.transportes.driver.feature.driver.data.local.dao.DriverDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MasterTransportesDatabase {
        return Room.databaseBuilder(
            context,
            MasterTransportesDatabase::class.java,
            "master_transportes.db"
        ).build()
    }

    @Provides
    fun provideDriverDao(
        database: MasterTransportesDatabase
    ): DriverDao = database.driverDao()
}