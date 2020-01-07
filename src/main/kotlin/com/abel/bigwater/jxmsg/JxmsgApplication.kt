package com.abel.bigwater.jxmsg

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@MapperScan(basePackages = ["com.abel.bigwater.data.mapper"])
class JxmsgApplication

fun main(args: Array<String>) {
    runApplication<JxmsgApplication>(*args)
}
