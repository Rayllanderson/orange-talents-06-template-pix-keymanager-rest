package br.com.zupacademy.rayllanderson.pix.details

import br.com.zupacademy.rayllanderson.*
import br.com.zupacademy.rayllanderson.core.factory.GrpcClientFactory
import br.com.zupacademy.rayllanderson.pix.utils.LocalDateTimeConverter
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
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
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class FindPixKeyDetailsControllerTest {

    @field:Inject
    @field:Client("/")
    lateinit var restClient: HttpClient

    @field:Inject
    lateinit var grpcClient: PixKeyFindDetailsServiceGrpc.PixKeyFindDetailsServiceBlockingStub

    var clientId = ""
    var baseUrl = ""

    @BeforeEach
    fun setup() {
        clientId = UUID.randomUUID().toString()
        baseUrl = "/api/v1/clients/$clientId/pix/keys"
    }

    @AfterEach
    fun cleanUp() {
        Mockito.reset(grpcClient)
    }

    @Test
    fun `should find pix key details`() {
        val expectedGrpcResponse = createPixKeyDetailsResponse()

        BDDMockito.`when`(grpcClient.find(Mockito.any())).thenReturn(expectedGrpcResponse)

        val pixId = expectedGrpcResponse.pixId

        val request = HttpRequest.GET<Any>("$baseUrl/$pixId")
        val response = restClient.toBlocking().exchange(request, Any::class.java)

        with(response) {
            assertEquals(HttpStatus.OK, status)
            assertNotNull(body())
        }
    }

    @Test
    fun `shouldn't find pix key details when pix id not found`() {

        val expectedError = StatusRuntimeException(Status.NOT_FOUND.withDescription("Pix id não encontrado"))
        BDDMockito.`when`(grpcClient.find(Mockito.any())).thenThrow(expectedError)


        val request = HttpRequest.GET<Any>("$baseUrl/${UUID.randomUUID()}")
        val error = assertThrows<HttpClientResponseException>{
            restClient.toBlocking().exchange(request, Any::class.java)
        }

        with(error) {
            assertEquals(HttpStatus.NOT_FOUND, status)
            assertEquals(expectedError.status.description, message)
        }
    }

    @Test
    fun `shouldn't find pix key details when clientId not found`() {

        val expectedError = StatusRuntimeException(Status.NOT_FOUND.withDescription("Client id não encontrado"))
        BDDMockito.`when`(grpcClient.find(Mockito.any())).thenThrow(expectedError)


        val request = HttpRequest.GET<Any>("$baseUrl/${UUID.randomUUID()}")
        val error = assertThrows<HttpClientResponseException>{
            restClient.toBlocking().exchange(request, Any::class.java)
        }

        with(error) {
            assertEquals(HttpStatus.NOT_FOUND, status)
            assertEquals(expectedError.status.description, message)
        }
    }

    @Test
    fun `shouldn't find pix key details when pix id not belong to client`() {

        val expectedError = StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription("Pix id não pertence ao cliente"))
        BDDMockito.`when`(grpcClient.find(Mockito.any())).thenThrow(expectedError)


        val request = HttpRequest.GET<Any>("$baseUrl/${UUID.randomUUID()}")
        val error = assertThrows<HttpClientResponseException>{
            restClient.toBlocking().exchange(request, Any::class.java)
        }

        with(error) {
            assertEquals(HttpStatus.BAD_REQUEST, status)
            assertEquals(expectedError.status.description, message)
        }
    }

    private fun createPixKeyDetailsResponse(): PixKeyDetailsResponse {
        return PixKeyDetailsResponse.newBuilder()
            .setClientId(clientId)
            .setPixId(UUID.randomUUID().toString())
            .setKeyType(KeyType.EMAIL)
            .setKey("kaguya@sama.com")
            .setOwnerName("Kaguya Sama")
            .setOwnerCpf("04536215623")
            .setAccount(createAccountDetails())
            .setCreatedAt(LocalDateTimeConverter.toProtobufTimestamp(LocalDateTime.now()))
            .build()
    }

    private fun createAccountDetails(): PixKeyDetailsResponse.Account {
        return PixKeyDetailsResponse.Account.newBuilder()
            .setAccountType(AccountType.CONTA_CORRENTE)
            .setBranch("0001")
            .setNumber("023546984")
            .setName("Itau")
            .build()
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class MockitoKeyFinderFactory {
        @Singleton
        internal fun mock() = Mockito.mock(PixKeyFindDetailsServiceGrpc.PixKeyFindDetailsServiceBlockingStub::class.java)
    }
}