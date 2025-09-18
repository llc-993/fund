package com.fund

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ManageApplication

fun main(args: Array<String>) {
	runApplication<ManageApplication>(*args)
	println("Manage 启动成功……")
}
