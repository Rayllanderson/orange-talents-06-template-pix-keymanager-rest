package br.com.zupacademy.rayllanderson.pix.list

import br.com.zupacademy.rayllanderson.PixKeyFindListServiceGrpc
import br.com.zupacademy.rayllanderson.PixKeyListRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.validation.constraints.NotBlank

@Validated
@Controller("/api/v1/clients/{clientId}/pix/keys")
class FindPixKeyListController(
    val grpcClient: PixKeyFindListServiceGrpc.PixKeyFindListServiceBlockingStub
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Get
    fun find(@NotBlank @PathVariable clientId: String): HttpResponse<List<FindPixKeyListResponse>> {

        logger.info("Cliente [$clientId] carregando lista de chave pix")

        val grpcResponse = grpcClient.find(PixKeyListRequest.newBuilder()
            .setClientId(clientId)
            .build()
        )

        val keys = grpcResponse.keysList.map{
            FindPixKeyListResponse(it)
        }

        return HttpResponse.ok(keys)
    }

}