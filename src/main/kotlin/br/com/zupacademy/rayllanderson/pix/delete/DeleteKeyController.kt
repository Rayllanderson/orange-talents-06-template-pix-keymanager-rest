package br.com.zupacademy.rayllanderson.pix.delete

import br.com.zupacademy.rayllanderson.PixKeyDeleteServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Validated
@Controller("/api/v1/clients/{id}/pix/keys")
class DeleteKeyController(
    val grpcClient: PixKeyDeleteServiceGrpc.PixKeyDeleteServiceBlockingStub
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Delete
    fun delete(@NotBlank @PathVariable id: String, @Body @Valid request: DeletePixKeyRequest): HttpResponse<Any>{
        val grpcRequest = request.toGrpcRequest(id)

        logger.info("Cliente $id deletando chave id ${request.pixId}")

        grpcClient.delete(grpcRequest)

        return HttpResponse.ok()
    }
}