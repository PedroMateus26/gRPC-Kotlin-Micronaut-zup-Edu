package com.pedromateus.zup.fretes

import com.google.protobuf.Any
import com.google.rpc.Code
import com.google.rpc.ErrorDetailsProto
import com.google.rpc.StatusProto
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FretesGrpcServer:FretesServiceGrpc.FretesServiceImplBase() {

    private val logger = LoggerFactory.getLogger(FretesGrpcServer::class.java)

    override fun calculaFrete(request: CalcularFreteRequest?, responseObserver: StreamObserver<CalculaFreteResponse>?) {

        logger.info("Calculando frete de $request")

        val cep = request?.cep

        if(cep==null||cep.isBlank()){
            val e = Status.INVALID_ARGUMENT
                .withDescription("cep deve ser informado!")
                .asRuntimeException()
            responseObserver?.onError(e)
        }

        //Simular umave verificação de segunrança
        if(cep!!.endsWith("333")){
            val statusProto=com.google.rpc.Status.newBuilder()
                .setCode(Code.PERMISSION_DENIED.number)
                .setMessage("Usuário não pode acessar esse recurso!")
                .addDetails(Any.pack(ErroDetails.newBuilder().setCode(401).setMessage("Token epirado!").build()))
                .build()
            val e = io.grpc.protobuf.StatusProto.toStatusRuntimeException(statusProto)
            responseObserver?.onError(e)
        }

        if(!cep!!.matches("[0-9]{5}-[0-9]{3}".toRegex())){
            val e = Status.INVALID_ARGUMENT
                .withDescription("cep inválido!")
                .augmentDescription(" O formato válido deve ser 99999-999.")
                .asRuntimeException()
            responseObserver?.onError(e)
        }

        var valor=0.0

        try {
            valor=Random.nextDouble(from = 0.0, until = 140.0)
            if(valor > 100){
                throw IllegalStateException("Valor inválido!")
            }
        }catch (e:Exception){

            responseObserver?.onError(
                Status.INTERNAL
                    .withDescription(e.message)
                    .withCause(e.cause)
                    .asRuntimeException()
            )

        }

        val response = CalculaFreteResponse.newBuilder()
            .setCep(request!!.cep)
            .setValor(valor)
            .build()

        responseObserver!!.onNext(response)
        responseObserver.onCompleted()
        logger.info("Frete calculado: $response")
    }
}