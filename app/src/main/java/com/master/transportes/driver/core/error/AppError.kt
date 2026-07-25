package com.master.transportes.driver.core.error

/**
 * Modelo Ãºnico de erro da aplicaÃ§Ã£o.
 *
 * Toda exceÃ§Ã£o â€” seja de rede, da JVM ou da API â€” Ã© convertida para um
 * AppError antes de chegar ao Repository.
 *
 * Usamos sealed class porque o Kotlin obriga a tratar todos os subtipos
 * em um when, evitando que um novo tipo de erro seja ignorado em
 * alguma tela no futuro.
 *
 * Os tipos desta sealed class representam duas categorias:
 *
 *   1) Erros de infraestrutura: Network, Timeout, SSL, Unknown
 *      â€” acontecem antes do servidor responder, pertencem ao Android.
 *
 *   2) Erros de negÃ³cio: Api
 *      â€” produzidos pelo backend com code, message e details.
 */
sealed class AppError {

    /** Sem conexÃ£o com a internet (DNS, roteamento, wifi desligado). */
    data object Network : AppError()

    /** Servidor demorou alÃ©m do limite configurado para responder. */
    data object Timeout : AppError()

    /** Falha de certificado ou handshake SSL/TLS. */
    data object SSL : AppError()

    /**
     * Erro imprevisto que nÃ£o se enquadra nas categorias acima.
     *
     * Preservamos o Throwable original para debugging sem expor
     * tipos concretos (IOException, NullPointerException etc.)
     * para as camadas de UI.
     */
    data class Unknown(
        val throwable: Throwable? = null
    ) : AppError()

    /**
     * Erro retornado pelo backend.
     *
     * Diferente dos tipos acima, este carrega dados porque a API
     * sempre envia um JSON estruturado com code, message e
     * opcionalmente details.errors para validaÃ§Ã£o por campo.
     *
     * Usamos um Ãºnico tipo para todos os erros de negÃ³cio.
     * O code (ex: "unauthenticated", "not_found") diferencia o
     * comportamento, nÃ£o subclasses â€” isso nÃ£o escala.
     */
    data class Api(
        val code: String,
        val message: String,
        val details: List<FieldError>? = null
    ) : AppError()
}

/**
 * Erro de validaÃ§Ã£o associado a um campo especÃ­fico do formulÃ¡rio.
 *
 * Exemplo retornado pela API:
 *   { "field": "login", "message": "E-mail invÃ¡lido" }
 */
data class FieldError(
    val field: String,
    val message: String
)
