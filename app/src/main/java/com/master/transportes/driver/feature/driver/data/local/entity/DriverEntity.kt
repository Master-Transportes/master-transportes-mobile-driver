package com.master.transportes.driver.feature.driver.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persistência local (Room) do perfil do motorista.
 *
 * O status do motorista é guardado como String (DriverStatus.name) para que o
 * banco não dependa do enum do domínio. A conversão acontece no mapper.
 *
 * O status online/offline NÃO é persistido aqui: ele é dinâmico e vive apenas
 * em memória no DriverSessionStore (decisão de arquitetura).
 */
@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey
    val id: String,

    val fullName: String,
    val email: String,
    val status: String,
    val rejectionReason: String?,
    val banReason: String?,
    val balanceInCents: Long,
)