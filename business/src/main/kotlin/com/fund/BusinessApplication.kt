package com.fund

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement


@SpringBootApplication
@EnableTransactionManagement
class BusinessApplication

fun main(args: Array<String>) {
    runApplication<BusinessApplication>(*args)
    println("business 启动成功……")
}
