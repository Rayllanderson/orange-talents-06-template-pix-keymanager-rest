package br.com.zupacademy.rayllanderson.pix.details

import br.com.zupacademy.rayllanderson.PixKeyDetailsRequest
import br.com.zupacademy.rayllanderson.PixKeyFindDetailsServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.validation.constraints.NotBlank

@Validated
@Controller("/api/v1/clients/{clientId}/pix/keys")
class FindPixKeyDetailsController(
    val grpcClient: PixKeyFindDetailsServiceGrpc.PixKeyFindDetailsServiceBlockingStub
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Get("/{pixId}")
    fun find(@NotBlank @PathVariable clientId: String, @NotBlank @PathVariable pixId: String): HttpResponse<Any> {

        logger.info("Cliente [$clientId] carregando chave pix de id [$pixId]")

        val grpcResponse = grpcClient.find(PixKeyDetailsRequest.newBuilder()
            .setClientId(clientId)
            .setPixId(pixId)
            .build()
        )

        return HttpResponse.ok(FindPixDetailsResponse(grpcResponse))
    }

}