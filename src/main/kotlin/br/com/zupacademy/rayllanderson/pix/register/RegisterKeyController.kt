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

@Validated
@Controller("/api/v1/pix/keys")
class RegisterKeyController(
    val grpcClient: PixKeyRegisterServiceGrpc.PixKeyRegisterServiceBlockingStub
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Post
    fun register(@Body @Valid request: RegisterPixKeyRequest): HttpResponse<Any> {

        logger.info("Cliente de id [${request.clientId}] está criando chave pix [${request.key} do tipo [${request.keyType}]")

        val response = grpcClient.register(request.toGrpcRequest())

        logger.info("Chave criada com sucesso")

        val uri = UriBuilder.of("/api/v1/pix/keys/{pixId}").expand(mutableMapOf("pixId" to response.pixId))

        return HttpResponse.created(uri)
    }

}