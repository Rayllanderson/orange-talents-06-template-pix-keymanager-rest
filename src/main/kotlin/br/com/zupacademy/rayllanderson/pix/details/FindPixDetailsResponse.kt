package br.com.zupacademy.rayllanderson.pix.details

import br.com.zupacademy.rayllanderson.AccountType
import br.com.zupacademy.rayllanderson.KeyType
import br.com.zupacademy.rayllanderson.PixKeyDetailsResponse
import br.com.zupacademy.rayllanderson.pix.utils.LocalDateTimeConverter
import java.time.LocalDateTime


class FindPixDetailsResponse(
    grpcResponse: PixKeyDetailsResponse,
) {
    val clientId: String = grpcResponse.clientId
    val pixId: String = grpcResponse.pixId
    val keyType: String = when(grpcResponse.keyType) {
        KeyType.CPF -> KeyType.CPF.toString()
        KeyType.EMAIL -> KeyType.EMAIL.toString()
        KeyType.PHONE -> KeyType.PHONE.toString()
        KeyType.RANDOM -> KeyType.RANDOM.toString()
        else -> "DESCONHECIDA"
    }
    val key: String = grpcResponse.key
    val ownerName: String = grpcResponse.ownerName
    val ownerCpf: String = grpcResponse.ownerCpf
    val account = AccountDetailsResponse(grpcResponse.account)
    val createdAt: LocalDateTime = LocalDateTimeConverter.fromProtobufTimeStamp(grpcResponse.createdAt)

    class AccountDetailsResponse(
        grpcAccountResponse: PixKeyDetailsResponse.Account
    ){
        val name: String = grpcAccountResponse.name
        val branch: String = grpcAccountResponse.branch
        val number: String = grpcAccountResponse.number
        val accountType: String = when(grpcAccountResponse.accountType) {
            AccountType.CONTA_POUPANCA -> AccountType.CONTA_POUPANCA.toString()
            AccountType.CONTA_CORRENTE -> AccountType.CONTA_CORRENTE.toString()
            else -> "DESCONHECIDA"
        }
    }
}



