package br.com.zupacademy.rayllanderson.pix.delete

import br.com.zupacademy.rayllanderson.PixKeyDeleteResponse
import br.com.zupacademy.rayllanderson.PixKeyDeleteServiceGrpc
import br.com.zupacademy.rayllanderson.core.factory.GrpcClientFactory
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.assertDoesNotThrow
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class DeleteKeyControllerTest {
    @field:Inject
    @field:Client("/")
    lateinit var restClient: HttpClient

    @field:Inject
    lateinit var grpcClient: PixKeyDeleteServiceGrpc.PixKeyDeleteServiceBlockingStub

    var clientId = ""
    var baseUrl = ""

    @BeforeEach
    fun setup(){
        clientId = UUID.randomUUID().toString()
        baseUrl = "/api/v1/clients/$clientId/pix/keys"
    }

    @AfterEach
    fun cleanUp() {
        Mockito.reset(grpcClient)
    }

    @Test
    fun `should remove pix key`() {

        val expectedGrpcResponse = PixKeyDeleteResponse.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .setClientId(clientId)
            .build()

        BDDMockito.`when`(grpcClient.delete(Mockito.any())).thenReturn(expectedGrpcResponse)

        val requestBody = DeletePixKeyRequest(expectedGrpcResponse.pixId)

        val request = HttpRequest.DELETE(baseUrl, requestBody)

        val response: HttpResponse<Any> = assertDoesNotThrow {
            restClient.toBlocking().exchange(request, Any::class.java)
        }

        assertEquals(HttpStatus.OK, response.status)
    }

    @Test
    fun `shouldn't remove pix key when pixId is empty`() {

        val requestBody = DeletePixKeyRequest("")

        val request = HttpRequest.DELETE(baseUrl, requestBody)

        val error = assertThrows<HttpClientResponseException> {
            restClient.toBlocking().exchange(request, Any::class.java)
        }

        assertEquals(HttpStatus.BAD_REQUEST, error.status)
        assertTrue(error.message!!.contains("Missing required creator property 'pixId'"))
    }

    @Test
    fun `shouldn't remove pix key when pixId not exists`() {

        val expectedError = StatusRuntimeException(Status.NOT_FOUND.withDescription("Pix id não encontrado"))
        BDDMockito.`when`(grpcClient.delete(Mockito.any())).thenThrow(expectedError)

        val requestBody = DeletePixKeyRequest(UUID.randomUUID().toString())

        val request = HttpRequest.DELETE(baseUrl, requestBody)

        val error = assertThrows<HttpClientResponseException> {
            restClient.toBlocking().exchange(request, Any::class.java)
        }

        assertEquals(HttpStatus.NOT_FOUND, error.status)
        assertEquals(expectedError.status.description, error.message)
    }

    @Test
    fun `shouldn't remove pix key when clientId not exists`() {

        val expectedError = StatusRuntimeException(Status.NOT_FOUND.withDescription("Client id não encontrado"))
        BDDMockito.`when`(grpcClient.delete(Mockito.any())).thenThrow(expectedError)

        val requestBody = DeletePixKeyRequest(UUID.randomUUID().toString())

        val request = HttpRequest.DELETE(baseUrl, requestBody)

        val error = assertThrows<HttpClientResponseException> {
            restClient.toBlocking().exchange(request, Any::class.java)
        }

        assertEquals(HttpStatus.NOT_FOUND, error.status)
        assertEquals(expectedError.status.description, error.message)
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class MockitoKeyRemoverFactory {
        @Singleton
        internal fun keyRemoverMock() =
            Mockito.mock(PixKeyDeleteServiceGrpc.PixKeyDeleteServiceBlockingStub::class.java)
    }
}
