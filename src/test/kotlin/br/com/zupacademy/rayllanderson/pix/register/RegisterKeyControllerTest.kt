package br.com.zupacademy.rayllanderson.pix.register

import br.com.zupacademy.rayllanderson.PixKeyRegisterResponse
import br.com.zupacademy.rayllanderson.PixKeyRegisterServiceGrpc
import br.com.zupacademy.rayllanderson.core.factory.GrpcClientFactory
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.BDDMockito.`when`
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RegisterKeyControllerTest {

    @field:Inject
    @field:Client("/")
    lateinit var restClient: HttpClient

    @field:Inject
    lateinit var grpcClient: PixKeyRegisterServiceGrpc.PixKeyRegisterServiceBlockingStub

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
    fun `should register new pix key`() {

        val expectedGrpcResponse = PixKeyRegisterResponse.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .build()

        `when`(grpcClient.register(Mockito.any())).thenReturn(expectedGrpcResponse)

        val requestBody = RegisterPixKeyRequest(
            keyType = KeyTypeRequest.EMAIL,
            key = "kaguya@sama.com",
            accountType = AccountTypeRequest.CONTA_CORRENTE
        )

        val request = HttpRequest.POST(baseUrl, requestBody)
        val response = restClient.toBlocking().exchange(request, RegisterPixKeyRequest::class.java)

        with(response) {
            assertThat(status).isEqualTo(HttpStatus.CREATED)
            assertThat(headers.contains(HttpHeaders.LOCATION)).isTrue
            assertThat(header(HttpHeaders.LOCATION)).contains(expectedGrpcResponse.pixId)
        }
    }

    @Test
    fun `should register new pix key when key is not present and type is random`() {

        val expectedGrpcResponse = PixKeyRegisterResponse.newBuilder()
            .setPixId(UUID.randomUUID().toString())
            .build()

        `when`(grpcClient.register(Mockito.any())).thenReturn(expectedGrpcResponse)

        val requestBody = RegisterPixKeyRequest(
            keyType = KeyTypeRequest.RANDOM,
            key = null,
            accountType = AccountTypeRequest.CONTA_CORRENTE
        )

        val request = HttpRequest.POST(baseUrl, requestBody)
        val response = restClient.toBlocking().exchange(request, RegisterPixKeyRequest::class.java)

        with(response) {
            assertThat(status).isEqualTo(HttpStatus.CREATED)
            assertThat(headers.contains(HttpHeaders.LOCATION)).isTrue
            assertThat(header(HttpHeaders.LOCATION)).contains(expectedGrpcResponse.pixId)
        }
    }

    @Test
    fun `shouldn't register new pix key when key already exists`() {

        val expectedError = StatusRuntimeException(Status.ALREADY_EXISTS)
        `when`(grpcClient.register(Mockito.any())).thenThrow(expectedError)

        val requestBody = RegisterPixKeyRequest(
            keyType = KeyTypeRequest.EMAIL,
            key = "kaguya@sama.com",
            accountType = AccountTypeRequest.CONTA_CORRENTE
        )

        val request = HttpRequest.POST(baseUrl, requestBody)
        val error = assertThrows<HttpClientResponseException> {
            restClient.toBlocking().exchange(request, RegisterPixKeyRequest::class.java)
        }

        with(error) {
            assertThat(status).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            assertThat(response.header(HttpHeaders.LOCATION)).isNull()
        }
    }

    @Test
    fun `shouldn't register new pix key when key is not formatted`() {

        val requestBody = RegisterPixKeyRequest(
            keyType = KeyTypeRequest.EMAIL,
            key = "kaguya-sama.com",
            accountType = AccountTypeRequest.CONTA_CORRENTE
        )

        val request = HttpRequest.POST(baseUrl, requestBody)
        val error = assertThrows<HttpClientResponseException> {
            restClient.toBlocking().exchange(request, RegisterPixKeyRequest::class.java)
        }

        with(error) {
            assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST)
            assertThat(message).isEqualTo("request: Chave pix está inválida. Exemplos de valores válidos:[CPF = 12345678901],[PHONE = +5585988714077],[RANDOM = Não preencher o valor da chave]")
            assertThat(response.header(HttpHeaders.LOCATION)).isNull()
        }
    }

    @Test
    fun `shouldn't register new pix key when key is not present and type is not random`() {

        val requestBody = RegisterPixKeyRequest(
            keyType = KeyTypeRequest.EMAIL,
            key = "",
            accountType = AccountTypeRequest.CONTA_CORRENTE
        )

        val request = HttpRequest.POST(baseUrl, requestBody)
        val error = assertThrows<HttpClientResponseException> {
            restClient.toBlocking().exchange(request, RegisterPixKeyRequest::class.java)
        }

        with(error) {
            assertThat(status).isEqualTo(HttpStatus.BAD_REQUEST)
            assertThat(message).isEqualTo("request: Chave pix está inválida. Exemplos de valores válidos:[CPF = 12345678901],[PHONE = +5585988714077],[RANDOM = Não preencher o valor da chave]")
            assertThat(response.header(HttpHeaders.LOCATION)).isNull()
        }
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class MockitoKeyRegisterFactory {
        @Singleton
        internal fun keyRegisterMock() = Mockito.mock(PixKeyRegisterServiceGrpc.PixKeyRegisterServiceBlockingStub::class.java)
    }
}