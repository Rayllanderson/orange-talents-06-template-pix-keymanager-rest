package br.com.zupacademy.rayllanderson.core.factory

import br.com.zupacademy.rayllanderson.PixKeyRegisterServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory(
    @GrpcChannel("pixKeyManager") val channel: ManagedChannel,
) {

    @Singleton
    fun registerKey() = PixKeyRegisterServiceGrpc.newBlockingStub(channel)

}