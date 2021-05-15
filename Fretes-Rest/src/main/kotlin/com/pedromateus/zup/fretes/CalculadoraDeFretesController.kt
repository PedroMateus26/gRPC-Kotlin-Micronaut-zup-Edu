package com.pedromateus.zup.fretes

import com.google.protobuf.Any
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.exceptions.HttpStatusException
import javax.inject.Inject

@Controller
class CalculadoraDeFretesController (@Inject val grpcClient:FretesServiceGrpc.FretesServiceBlockingStub){

    @Get("/api/fretes")
    fun calcula(@QueryValue cep:String):FreteResponse{
        val request = CalcularFreteRequest.newBuilder()
            .setCep(cep)
            .build()

        try {
            val response = grpcClient.calculaFrete(request)
            return FreteResponse(cep = response.cep,valor = response.valor)
        }catch (e:StatusRuntimeException){
            val statusCode=e.status.code
            val descricao=e.status.description

            if(statusCode==Status.Code.INVALID_ARGUMENT){
                throw HttpStatusException(HttpStatus.BAD_REQUEST,descricao)
            }

            if(statusCode==Status.Code.PERMISSION_DENIED){

                val statusProto=StatusProto.fromThrowable(e)?: throw HttpStatusException(HttpStatus.FORBIDDEN,descricao)
                val anyDetails:Any=statusProto.detailsList[0]
                val errorDetails = anyDetails.unpack(ErrorDetails::class.java)
                throw HttpStatusException(HttpStatus.FORBIDDEN,"${errorDetails.code}: ${errorDetails.message}")

            }
            return throw HttpStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        }


    }
}

data class FreteResponse (@QueryValue val cep:String, val valor:Double){

}
