package br.com.zupacademy.rayllanderson.core.exceptions

import io.grpc.Status
import io.grpc.StatusException
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class StatusRuntimeExceptionHandlerTest{

    @Test
    fun `should return BAD_REQUEST when grpc server returns INVALID_ARGUMENT `(){
        val grpcException = StatusRuntimeException(Status.INVALID_ARGUMENT)

        val request = HttpRequest.GET<Any>("/")

        val error = StatusRuntimeExceptionHandler().handle(request, grpcException)

        assertEquals(error.status, HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `should return UNPROCESSABLE_ENTITY when grpc server returns ALREADY_EXISTS `(){
        val grpcException = StatusRuntimeException(Status.ALREADY_EXISTS)

        val request = HttpRequest.GET<Any>("/")

        val error = StatusRuntimeExceptionHandler().handle(request, grpcException)

        assertEquals(error.status, HttpStatus.UNPROCESSABLE_ENTITY)
    }


    @Test
    fun `should return NOT_FOUND when grpc server returns NOT_FOUND `(){
        val grpcException = StatusRuntimeException(Status.NOT_FOUND)

        val request = HttpRequest.GET<Any>("/")

        val error = StatusRuntimeExceptionHandler().handle(request, grpcException)

        assertEquals(error.status, HttpStatus.NOT_FOUND)
    }

    @Test
    fun `should return FORBIDDEN when grpc server returns PERMISSION_DENIED `(){
        val grpcException = StatusRuntimeException(Status.PERMISSION_DENIED)

        val request = HttpRequest.GET<Any>("/")

        val error = StatusRuntimeExceptionHandler().handle(request, grpcException)

        assertEquals(error.status, HttpStatus.FORBIDDEN)
    }

    @Test
    fun `should return INTERNAL_SERVER_ERROR when grpc server returns INTERNAL `(){
        val grpcException = StatusRuntimeException(Status.INTERNAL)

        val request = HttpRequest.GET<Any>("/")

        val error = StatusRuntimeExceptionHandler().handle(request, grpcException)

        assertEquals(error.status, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun `should return BAD_GATEWAY when grpc server returns UNKNOW `(){
        val grpcException = StatusRuntimeException(Status.UNKNOWN)

        val request = HttpRequest.GET<Any>("/")

        val error = StatusRuntimeExceptionHandler().handle(request, grpcException)

        assertEquals(error.status, HttpStatus.BAD_GATEWAY)
    }
}