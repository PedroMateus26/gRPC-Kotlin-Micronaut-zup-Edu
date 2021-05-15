package br.com.zup.edu

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class CarrosEndpointTest(val grpcClient:CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub, val repository: CarroRepository){

    /**
     * Happy path
     * quando já existe carro com a placa
     * quando os dados de entradas são inválidos
     */

    @BeforeEach
    fun setup(){
        repository.deleteAll()
    }

    @Test
    fun `deve retornar um novo carro`(){
        //cenário
            //colocado no before each

        //ação
        val response = grpcClient.adicionar(CarroRequest.newBuilder()
            .setModelo("Gol")
            .setPlaca("AAA-1234")
            .build())

        //validade
        with(response){
            assertNotNull(id)
            assertTrue(repository.existsById(id))
        }

    }

    @Test
    fun `não deve adicionar novo carro quando carro já existe`(){
        //cenáirio
        val existente=repository.save(Carro("Pálio", "AAA-1234"))

        //ação
        val response=assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(CarroRequest.newBuilder()
                .setModelo("Ferrari")
                .setPlaca(existente.placa)
                .build())
        }


        //validação
        with(response){
            assertEquals(Status.ALREADY_EXISTS.code,status.code)
            assertEquals("carro com placa existente",status.description)

        }

    }

    @Test
    fun `não deve adicionar novo carro quando os dados forem inválidos`(){
        //cenáirio
        val existente=repository.save(Carro("Pálio", "AAA-1234"))

        //ação
        val response=assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(CarroRequest.newBuilder().build())
        }


        //validação
        with(response){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("dados de entrada inválidos",status.description)

        }

    }


}

@Factory
class Clients{
    @Singleton
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel:ManagedChannel): CarrosGrpcServiceGrpc.CarrosGrpcServiceBlockingStub? {
        return CarrosGrpcServiceGrpc.newBlockingStub(channel)
    }
}

