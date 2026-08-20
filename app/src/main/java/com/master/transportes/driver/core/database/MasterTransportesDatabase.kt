package com.master.transportes.driver.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.master.transportes.driver.feature.driver.data.local.dao.DriverDao
import com.master.transportes.driver.feature.driver.data.local.entity.DriverEntity

/**
 * Banco local do aplicativo.
 *
 * Ponto de junção das entidades de todas as features. Novas entidades
 * (Vehicle, Ride, RideOffer, Wallet...) são adicionadas aqui com version++
 * e uma migration.
 */
@Database(
    entities = [
        DriverEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class MasterTransportesDatabase : RoomDatabase() {

    abstract fun driverDao(): DriverDao
}