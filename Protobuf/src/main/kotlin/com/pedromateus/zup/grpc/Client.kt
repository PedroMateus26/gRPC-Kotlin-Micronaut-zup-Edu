package com.pedromateus.zup.grpc

import io.grpc.ManagedChannelBuilder

fun main() {
    val channel = ManagedChannelBuilder
        .forAddress("localhost", 50051)
        .usePlaintext()
        .build()

    val request = FuncionarioRequest
        .newBuilder()
        .setNome("Pedro Mateus")
        .setCpf("111.111.111-11")
        .setSalario(2000.20)
        .setAtivo(true)
        .setCargo(Cargo.DEV)
        .addEnderecos(
            FuncionarioRequest.Endereco.newBuilder()
                .setLogradouro("Orlando Pedro da Silva")
                .setCep("38702-108")
                .setComplemento("Casa 92")
                .build()
        )
        .build()

    val client = FuncionarioServiceGrpc.newBlockingStub(channel)
    val response = client.cadastrar(request)
    println(response)

}