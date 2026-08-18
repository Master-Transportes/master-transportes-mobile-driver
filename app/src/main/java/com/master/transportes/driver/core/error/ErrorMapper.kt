package com.master.transportes.driver.core.error

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/**
 * Tradutor de exceções técnicas para AppError (modelo da aplicação).
 *
 * O restante da aplicação (ViewModel, Repository, UI) nunca conhece
 * HttpException, IOException ou qualquer exceção do Retrofit/JVM.
 * Tudo passa por este mapper e vira AppError.
 *
 * Se o Retrofit for trocado por Ktor ou outra lib no futuro,
 * apenas este arquivo precisará ser alterado.
 */
object ErrorMapper {

    /**
     * Instância do Gson para ler o errorBody() das respostas HTTP.
     *
     * Gson converte JSON em objetos Kotlin. Usamos ele aqui porque
     * o projeto já o utiliza no Retrofit — não faz sentido adicionar
     * outra lib de serialização só para isso.
     */
    private val gson = Gson()

    /**
     * Converte qualquer exceção lançada pela camada de rede em AppError.
     *
     * Cada bloco do when representa uma categoria de erro:
     *   - infraestrutura: problemas que acontecem antes da resposta
     *   - HttpException:  servidor respondeu, o body contém o erro
     *   - Unknown:        tudo que não se encaixa acima
     */
    fun map(e: Exception): AppError = when (e) {
        // Cancelamento da coroutine nunca deve virar erro de UI.
        is CancellationException -> throw e

        // DNS ou roteamento falhou — o dispositivo não conseguiu
        // encontrar o servidor. É um erro de conectividade.
        is UnknownHostException -> AppError.Network

        // O servidor recebeu a requisição mas não respondeu
        // dentro do prazo configurado no OkHttp.
        is SocketTimeoutException -> AppError.Timeout

        // A conexão TCP foi recusada pelo servidor.
        // Tratamos como Network porque a causa raiz é a mesma:
        // o servidor está inacessível.
        is ConnectException -> AppError.Network

        // Problema de certificado digital ou handshake SSL/TLS.
        // Mantemos separado de Network para que no futuro
        // possamos identificar falhas de certificado específicas.
        is SSLException -> AppError.SSL

        // O servidor respondeu com um código HTTP (4xx ou 5xx)
        // e o body contém um JSON de erro estruturado.
        // Diferente dos casos acima, aqui temos dados para extrair.
        is HttpException -> mapHttpException(e)

        // JSON da resposta malformado ou fora do contrato esperado.
        // Importante: o branch precisa vir ANTES do IOException porque
        // MalformedJsonException é um IOException e seria engolido por ele.
        is JsonSyntaxException,
        is MalformedJsonException,
        is JsonParseException -> AppError.Serialization

        // Qualquer outro erro de entrada/saída não categorizado acima.
        // Exemplos: falha de leitura/escrita, stream corrompido.
        is IOException -> AppError.Unknown(e)

        // Exceção imprevista (NullPointerException,
        // IllegalStateException, etc.). Sempre existe um
        // erro desconhecido — o importante é não quebrar o app.
        else -> AppError.Unknown(e)
    }

    /**
     * Lê o corpo da resposta HTTP e o converte em AppError.Api.
     *
     * Não usamos apenas o código HTTP (401, 404, 422) porque:
     *   - o backend sempre retorna um JSON com code, message e details
     *   - a mensagem é específica do contexto (ex: "Conta banida.")
     *   - os detalhes de validação por campo estão no body
     *
     * Se o body estiver vazio, for nulo, ou não seguir o contrato
     * esperado (ApiErrorResponse), caímos em AppError.Api com o
     * código HTTP como fallback, preservando o status real do servidor.
     */
    private fun mapHttpException(e: HttpException): AppError {
        return try {
            val body = e.response()?.errorBody()?.string()
            if (body.isNullOrBlank()) {
                return AppError.Api(
                    code = e.code().toString(),
                    message = "Erro inesperado"
                )
            }
            val apiError = gson.fromJson(body, ApiErrorResponse::class.java)
            if (apiError.code.isNullOrBlank() || apiError.message.isNullOrBlank()) {
                return AppError.Api(
                    code = e.code().toString(),
                    message = "Erro inesperado"
                )
            }
            AppError.Api(
                code = apiError.code,
                message = apiError.message,
                details = apiError.details?.errors
            )
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            AppError.Api(
                code = e.code().toString(),
                message = "Erro inesperado"
            )
        }
    }
}
