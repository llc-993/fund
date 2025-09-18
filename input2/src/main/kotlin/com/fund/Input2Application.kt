package com.fund

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class Input2Application

fun main(args: Array<String>) {
    runApplication<Input2Application>(*args)
}
