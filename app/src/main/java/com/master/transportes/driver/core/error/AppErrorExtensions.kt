package com.master.transportes.driver.core.error

fun AppError.toUserMessage(): String = when (this) {
    AppError.Network -> "Sem conexão com a internet."
    AppError.Timeout -> "Tempo de conexão esgotado."
    AppError.SSL -> "Falha de segurança na conexão."
    is AppError.Unknown -> "Erro inesperado. Tente novamente."
    is AppError.Api -> message
    AppError.Serialization -> "Resposta do servidor inesperada. Tente novamente."
}