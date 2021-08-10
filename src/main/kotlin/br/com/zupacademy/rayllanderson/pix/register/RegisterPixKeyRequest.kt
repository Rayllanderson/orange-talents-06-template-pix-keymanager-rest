package br.com.zupacademy.rayllanderson.pix.register

import br.com.zupacademy.rayllanderson.AccountType
import br.com.zupacademy.rayllanderson.KeyType
import br.com.zupacademy.rayllanderson.PixKeyRegisterRequest
import br.com.zupacademy.rayllanderson.core.validators.ValidPixKey
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidPixKey
@Introspected
class RegisterPixKeyRequest(
    @field:NotBlank
    val clientId: String,

    @field:NotNull
    val keyType: KeyTypeRequest,

    @field:Size(max = 77)
    val key: String?,

    @field:NotNull
    val accountType: AccountTypeRequest,
) {
    fun toGrpcRequest(): PixKeyRegisterRequest {
        return PixKeyRegisterRequest.newBuilder()
            .setClientId(this.clientId)
            .setKeyType(this.keyType.keyTypeGrpc)
            .setKey(this.key ?: "")
            .setAccountType(this.accountType.accountTypeGrpc)
            .build()
    }
}

enum class AccountTypeRequest(
    val accountTypeGrpc: AccountType,
) {
    CONTA_CORRENTE(AccountType.CONTA_CORRENTE),
    CONTA_POUPANCA(AccountType.CONTA_POUPANCA)
}

enum class KeyTypeRequest(
    val keyTypeGrpc: KeyType
){
    CPF(KeyType.CPF) {
        override fun validate(value: String?): Boolean {
            if(value.isNullOrBlank()) return false
            return value.matches("^[0-9]{11}\$".toRegex())
        }
    },
    PHONE(KeyType.PHONE) {
        override fun validate(value: String?) : Boolean {
            if(value.isNullOrBlank()) return false
            return value.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL(KeyType.EMAIL) {
        override fun validate(value: String?) : Boolean {
            if(value.isNullOrBlank()) return false
            return value.matches("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+".toRegex())
        }
    },
    RANDOM(KeyType.RANDOM) {
        override fun validate(value: String?) = value.isNullOrBlank()
    };

    abstract fun validate(value: String?): Boolean
}
