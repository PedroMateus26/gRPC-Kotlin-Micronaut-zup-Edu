package com.pedromateus.zup.grpc

import java.io.FileInputStream
import java.io.FileOutputStream

fun main() {
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

    println(request)
    request.writeTo(FileOutputStream("funcionario-request.bin"))

    val request2=FuncionarioRequest.newBuilder().mergeFrom(FileInputStream("funcionario-request.bin"))

    request2.setCargo(Cargo.GERENTE)
    println(request2)
}

