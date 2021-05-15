package com.pedromateus.zup.grpc

import com.google.protobuf.Timestamp
import io.grpc.ServerBuilder
import io.grpc.stub.StreamObserver
import java.time.LocalDateTime
import java.time.ZoneId

fun main(){
    val server=ServerBuilder.forPort(50051)
        .addService(FuncionarioEndPoint())
        .build()

    server.start()
    server.awaitTermination()
}

class FuncionarioEndPoint : FuncionarioServiceGrpc.FuncionarioServiceImplBase(){

    override fun cadastrar(request: FuncionarioRequest?, responseObserver: StreamObserver<FuncionarioResponse>?) {
        val nome = request?.nome
        val instant = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant()
        val response = FuncionarioResponse.newBuilder().build()
        val criadoEm = Timestamp.newBuilder()
            .setSeconds(instant.epochSecond)
            .setNanos(instant.nano)
            .build()

        val request = FuncionarioResponse.newBuilder()
            .setNome(nome)
            .setCriadoEm(criadoEm)
            .build()

        responseObserver?.onNext(request)
        responseObserver?.onCompleted()
    }
}