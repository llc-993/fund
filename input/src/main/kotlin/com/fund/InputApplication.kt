package com.fund

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class InputApplication

fun main(args: Array<String>) {
    runApplication<InputApplication>(*args)
    println("input 启动成功……")
}
