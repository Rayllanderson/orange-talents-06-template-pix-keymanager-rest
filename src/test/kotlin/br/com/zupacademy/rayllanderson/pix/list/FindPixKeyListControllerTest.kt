package br.com.zupacademy.rayllanderson.pix.list

import br.com.zupacademy.rayllanderson.AccountType
import br.com.zupacademy.rayllanderson.KeyType
import br.com.zupacademy.rayllanderson.PixKeyDetailsResponse.Account
import br.com.zupacademy.rayllanderson.PixKeyFindListServiceGrpc
import br.com.zupacademy.rayllanderson.PixKeyListResponse
import br.com.zupacademy.rayllanderson.core.factory.GrpcClientFactory
import br.com.zupacademy.rayllanderson.pix.utils.LocalDateTimeConverter
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@MicronautTest
internal class FindPixKeyListControllerTest{

    @field:Inject
    @field:Client("/")
    lateinit var restClient: HttpClient

    @field:Inject
    lateinit var grpcClient: PixKeyFindListServiceGrpc.PixKeyFindListServiceBlockingStub

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
    fun `should find list of pix key `() {
        val firstSavedKey = createGrpcPixKeyResponse("kaguya@sama.com", KeyType.EMAIL, AccountType.CONTA_POUPANCA)
        val secondSavedKey = createGrpcPixKeyResponse("056384635178", KeyType.CPF, AccountType.CONTA_CORRENTE)
        val expectedGrpcResponse = PixKeyListResponse.newBuilder().addAllKeys(listOf(firstSavedKey, secondSavedKey)).build()

        BDDMockito.`when`(grpcClient.find(Mockito.any())).thenReturn(expectedGrpcResponse)

        val request = HttpRequest.GET<Any>(baseUrl)
        val response = restClient.toBlocking().exchange(request, List::class.java)

        val expectedSize = expectedGrpcResponse.keysCount
        with(response) {
            assertThat(status).isEqualTo(HttpStatus.OK)
            assertThat(body()).isNotNull.isNotEmpty.hasSize(expectedSize)
        }
    }

    @Test
    fun `should find empty list of pix key when client does not have any key`() {
        val expectedGrpcResponse = PixKeyListResponse.newBuilder().addAllKeys(emptyList()).build()

        BDDMockito.`when`(grpcClient.find(Mockito.any())).thenReturn(expectedGrpcResponse)

        val request = HttpRequest.GET<Any>(baseUrl)
        val response = restClient.toBlocking().exchange(request, List::class.java)

        with(response) {
            assertThat(status).isEqualTo(HttpStatus.OK)

            assertThat(body()).isNotNull.isEmpty()
        }
    }

    private fun createGrpcPixKeyResponse(key: String, keyType: KeyType, accountType: AccountType): PixKeyListResponse.PixKey {
        return PixKeyListResponse.PixKey.newBuilder()
            .setClientId(clientId)
            .setPixId(UUID.randomUUID().toString())
            .setKeyType(keyType)
            .setKey(key)
            .setAccountType(accountType)
            .setCreatedAt(LocalDateTimeConverter.toProtobufTimestamp(LocalDateTime.now()))
            .build()
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class MockitoKeyFinderFactory {
        @Singleton
        internal fun mock() = Mockito.mock(PixKeyFindListServiceGrpc.PixKeyFindListServiceBlockingStub::class.java)
    }
}