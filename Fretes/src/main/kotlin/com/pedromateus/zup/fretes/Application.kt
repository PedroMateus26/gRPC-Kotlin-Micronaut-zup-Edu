package com.pedromateus.zup.fretes

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("com.pedromateus.zup.fretes")
		.start()
}

