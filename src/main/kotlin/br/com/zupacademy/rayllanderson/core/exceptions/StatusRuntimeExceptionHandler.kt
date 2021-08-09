package br.com.zupacademy.rayllanderson.core.exceptions

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.server.exceptions.ExceptionHandler
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class StatusRuntimeExceptionHandler: ExceptionHandler<StatusRuntimeException, HttpResponse<JsonError>> {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handle(request: HttpRequest<*>?, e: StatusRuntimeException?): HttpResponse<JsonError> {
        var status = HttpStatus.INTERNAL_SERVER_ERROR

        if (e != null) {
            status = when (e.status.code) {
                Status.INVALID_ARGUMENT.code -> HttpStatus.BAD_REQUEST
                Status.ALREADY_EXISTS.code -> HttpStatus.UNPROCESSABLE_ENTITY
                Status.NOT_FOUND.code -> HttpStatus.NOT_FOUND
                Status.PERMISSION_DENIED.code -> HttpStatus.FORBIDDEN
                Status.INTERNAL.code -> HttpStatus.INTERNAL_SERVER_ERROR
                else -> HttpStatus.BAD_GATEWAY
            }
        }

        logger.error("Ocorreu um erro. Status: [$status], descrição: [${e?.status?.description}")

        val jsonError = JsonError(e?.status?.description ?: "Ocorreu um erro")
        return HttpResponse.status<JsonError?>(status).body(jsonError)
    }
}