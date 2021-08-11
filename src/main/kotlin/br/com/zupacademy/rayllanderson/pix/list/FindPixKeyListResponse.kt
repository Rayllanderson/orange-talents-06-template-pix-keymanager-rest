package br.com.zupacademy.rayllanderson.pix.list

import br.com.zupacademy.rayllanderson.AccountType
import br.com.zupacademy.rayllanderson.KeyType
import br.com.zupacademy.rayllanderson.PixKeyDetailsResponse
import br.com.zupacademy.rayllanderson.PixKeyListResponse
import br.com.zupacademy.rayllanderson.pix.utils.LocalDateTimeConverter
import java.time.LocalDateTime


class FindPixKeyListResponse(
    pixKey: PixKeyListResponse.PixKey,
) {
    val clientId: String = pixKey.clientId
    val pixId: String = pixKey.pixId
    val keyType: String = pixKey.keyType.toString()
    val key: String = pixKey.key
    val accountType: String = when(pixKey.accountType) {
        AccountType.CONTA_POUPANCA -> AccountType.CONTA_POUPANCA.toString()
        AccountType.CONTA_CORRENTE -> AccountType.CONTA_CORRENTE.toString()
        else -> "DESCONHECIDA"
    }
    val createdAt: LocalDateTime = LocalDateTimeConverter.fromProtobufTimeStamp(pixKey.createdAt)
}



