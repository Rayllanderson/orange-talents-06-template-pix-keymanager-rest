package br.com.zupacademy.rayllanderson.pix.delete

import br.com.zupacademy.rayllanderson.PixKeyDeleteRequest
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
class DeletePixKeyRequest(

    @field:NotBlank
    val pixId: String,

) {
    fun toGrpcRequest(clientId: String): PixKeyDeleteRequest? {
        return PixKeyDeleteRequest.newBuilder()
            .setClientId(clientId)
            .setPixId(this.pixId)
            .build()
    }
}
