package br.com.zupacademy.rayllanderson.pix.register

import br.com.zupacademy.rayllanderson.PixKeyRegisterServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.uri.UriBuilder
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
@Controller("/api/v1/clients/{id}/pix/keys")
class RegisterKeyController(
    val grpcClient: PixKeyRegisterServiceGrpc.PixKeyRegisterServiceBlockingStub
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Post
    fun register(@NotBlank id: String, @Body @Valid request: RegisterPixKeyRequest): HttpResponse<Any> {

        logger.info("Cliente id [$id] está criando chave pix [${request}]")

        val response = grpcClient.register(request.toGrpcRequest(id))

        logger.info("Chave criada com sucesso")

        val uri = UriBuilder.of("/api/v1/clients/{id}/pix/keys/{pixId}").expand(
            mutableMapOf("id" to id, "pixId" to response.pixId)
        )

        return HttpResponse.created(uri)
    }

}