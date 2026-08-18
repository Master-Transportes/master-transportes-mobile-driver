package com.master.transportes.driver.core.error

/**
 * Modelo único de erro da aplicação.
 *
 * Toda exceção — seja de rede, da JVM ou da API — é convertida para um
 * AppError antes de chegar ao Repository.
 *
 * Usamos sealed class porque o Kotlin obriga a tratar todos os subtipos
 * em um when, evitando que um novo tipo de erro seja ignorado em
 * alguma tela no futuro.
 *
 * Os tipos desta sealed class representam duas categorias:
 *
 *   1) Erros de infraestrutura: Network, Timeout, SSL, Unknown
 *      — acontecem antes do servidor responder, pertencem ao Android.
 *
 *   2) Erros de negócio: Api
 *      — produzidos pelo backend com code, message e details.
 */
sealed class AppError {

    /** Sem conexão com a internet (DNS, roteamento, wifi desligado). */
    data object Network : AppError()

    /** Servidor demorou além do limite configurado para responder. */
    data object Timeout : AppError()

    /** Falha de certificado ou handshake SSL/TLS. */
    data object SSL : AppError()

    /**
     * Erro imprevisto que não se enquadra nas categorias acima.
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
     * opcionalmente details.errors para validação por campo.
     *
     * Usamos um único tipo para todos os erros de negócio.
     * O code (ex: "unauthenticated", "not_found") diferencia o
     * comportamento, não subclasses — isso não escala.
     */
    data class Api(
        val code: String,
        val message: String,

        /**
         * Detalhes de validação por campo.
         *
         * Reservado para quando a API passar a retornar erros específicos
         * (ex.: { "field": "login", "message": "E-mail inválido" }).
         * Hoje o backend retorna apenas uma mensagem geral, mas mantemos
         * este campo para não quebrar o contrato quando isso mudar.
         */
        val details: List<FieldError>? = null
    ) : AppError()

    /**
     * JSON de resposta fora do contrato esperado (malformado/esquema inválido).
     *
     * Difere de Unknown para que uma falha de serialização seja rastreável
     * separadamente: normalmente significa que o backend mudou o contrato
     * da API sem avisar.
     */
    data object Serialization : AppError()
}

/**
 * Erro de validação associado a um campo específico do formulário.
 *
 * Exemplo retornado pela API:
 *   { "field": "login", "message": "E-mail inválido" }
 */
data class FieldError(
    val field: String,
    val message: String
)