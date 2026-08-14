package com.master.transportes.driver.core.error

import com.google.gson.annotations.SerializedName

/**
 * DTO que representa exatamente o JSON de erro retornado pelo backend.
 *
 * O ErrorMapper desserializa o errorBody() das HttpException para
 * este modelo. Manter a estrutura idêntica ao contrato da API garante
 * que nenhuma informação enviada pelo backend seja descartada.
 */
data class ApiErrorResponse(
    val code: String,
    val message: String,
    val details: ApiErrorDetails? = null,
    @SerializedName("internal_message")
    val internalMessage: String? = null
)

/**
 * O backend pode incluir uma lista de erros por campo
 * no campo "details.errors" do JSON de resposta.
 */
data class ApiErrorDetails(
    val errors: List<FieldError>? = null
)