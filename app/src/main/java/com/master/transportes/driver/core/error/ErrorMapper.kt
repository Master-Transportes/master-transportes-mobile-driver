package com.master.transportes.driver.core.error

import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/**
 * Tradutor de exceÃ§Ãµes tÃ©cnicas para AppError (modelo da aplicaÃ§Ã£o).
 *
 * O restante da aplicaÃ§Ã£o (ViewModel, Repository, UI) nunca conhece
 * HttpException, IOException ou qualquer exceÃ§Ã£o do Retrofit/JVM.
 * Tudo passa por este mapper e vira AppError.
 *
 * Se o Retrofit for trocado por Ktor ou outra lib no futuro,
 * apenas este arquivo precisarÃ¡ ser alterado.
 */
object ErrorMapper {

    /**
     * InstÃ¢ncia do Gson para ler o errorBody() das respostas HTTP.
     *
     * Gson converte JSON em objetos Kotlin. Usamos ele aqui porque
     * o projeto jÃ¡ o utiliza no Retrofit â€” nÃ£o faz sentido adicionar
     * outra lib de serializaÃ§Ã£o sÃ³ para isso.
     */
    private val gson = Gson()

    /**
     * Converte qualquer exceÃ§Ã£o lanÃ§ada pela camada de rede em AppError.
     *
     * Cada bloco do when representa uma categoria de erro:
     *   - infraestrutura: problemas que acontecem antes da resposta
     *   - HttpException:  servidor respondeu, o body contÃ©m o erro
     *   - Unknown:        tudo que nÃ£o se encaixa acima
     */
    fun map(e: Exception): AppError = when (e) {
        // DNS ou roteamento falhou â€” o dispositivo nÃ£o conseguiu
        // encontrar o servidor. Ã‰ um erro de conectividade.
        is UnknownHostException -> AppError.Network

        // O servidor recebeu a requisiÃ§Ã£o mas nÃ£o respondeu
        // dentro do prazo configurado no OkHttp.
        is SocketTimeoutException -> AppError.Timeout

        // A conexÃ£o TCP foi recusada pelo servidor.
        // Tratamos como Network porque a causa raiz Ã© a mesma:
        // o servidor estÃ¡ inacessÃ­vel.
        is ConnectException -> AppError.Network

        // Problema de certificado digital ou handshake SSL/TLS.
        // Mantemos separado de Network para que no futuro
        // possamos identificar falhas de certificado especÃ­ficas.
        is SSLException -> AppError.SSL

        // O servidor respondeu com um cÃ³digo HTTP (4xx ou 5xx)
        // e o body contÃ©m um JSON de erro estruturado.
        // Diferente dos casos acima, aqui temos dados para extrair.
        is HttpException -> mapHttpException(e)

        // Qualquer outro erro de entrada/saÃ­da nÃ£o categorizado acima.
        // Exemplos: falha de leitura/escrita, stream corrompido.
        is IOException -> AppError.Unknown(e)

        // ExceÃ§Ã£o imprevista (NullPointerException,
        // IllegalStateException, etc.). Sempre existe um
        // erro desconhecido â€” o importante Ã© nÃ£o quebrar o app.
        else -> AppError.Unknown(e)
    }

    /**
     * LÃª o corpo da resposta HTTP e o converte em AppError.Api.
     *
     * NÃ£o usamos apenas o cÃ³digo HTTP (401, 404, 422) porque:
     *   - o backend sempre retorna um JSON com code, message e details
     *   - a mensagem Ã© especÃ­fica do contexto (ex: "Conta banida.")
     *   - os detalhes de validaÃ§Ã£o por campo estÃ£o no body
     *
     * Se o body estiver vazio, for nulo ou nÃ£o seguir o contrato
     * esperado (ApiErrorResponse), caÃ­mos em AppError.Unknown
     * para evitar propagar um erro com dados inconsistentes.
     */
    private fun mapHttpException(e: HttpException): AppError {
        return try {
            val body = e.response()?.errorBody()?.string()
            if (body.isNullOrBlank()) {
                return AppError.Unknown(e)
            }
            val apiError = gson.fromJson(body, ApiErrorResponse::class.java)
            if (apiError.code.isNullOrBlank() || apiError.message.isNullOrBlank()) {
                return AppError.Unknown(e)
            }
            AppError.Api(
                code = apiError.code,
                message = apiError.message,
                details = apiError.details?.errors
            )
        } catch (_: Exception) {
            AppError.Unknown(e)
        }
    }
}
