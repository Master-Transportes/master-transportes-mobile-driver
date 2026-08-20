package com.master.transportes.driver.feature.driver.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.master.transportes.driver.feature.driver.data.local.entity.DriverEntity
import kotlinx.coroutines.flow.Flow

/**
 * Acesso local ao perfil do motorista.
 *
 * Existe apenas um motorista autenticado por instalação, então usamos LIMIT 1.
 * A consistência é garantida pelo clear() no logout — o banco nunca mantém
 * dados de uma sessão anterior.
 *
 * A UI nunca chama este DAO diretamente: tudo passa pelo DriverRepository
 * (observeDriver) e pelo DriverSessionStore.
 */
@Dao
interface DriverDao {

    @Query("SELECT * FROM drivers LIMIT 1")
    fun observeDriver(): Flow<DriverEntity?>

    @Upsert
    suspend fun upsert(driver: DriverEntity)

    @Query("DELETE FROM drivers")
    suspend fun clear()
}