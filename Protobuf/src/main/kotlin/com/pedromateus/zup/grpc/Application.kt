package com.pedromateus.zup.grpc

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("com.pedromateus.zup.grpc")
		.start()
}

