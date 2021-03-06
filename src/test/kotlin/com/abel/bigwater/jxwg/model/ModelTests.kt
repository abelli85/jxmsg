package com.abel.bigwater.jxwg.model

import com.abel.bigwater.jxmsg.FlowMsg
import com.alibaba.fastjson.JSON
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.util.Assert

class ModelTests {
    @Test
    fun testParseMeter() {
        val str = """
            {
    "resultCode": "0050000",
    "message": {
        "description": "查询成功"
    },
    "count": 1,
    "queryData": [
        {
            "id": "3e7554f5-9e91-4572-9939-e4abf888c55b",
            "modelId": "3e7554f5-9e91-4572-9939-e4abf888c55b",
            "creator": 1,
            "createTime": "2018-02-09 10:43:45",
            "operateTime": "2018-03-16 15:37:27",
            "basic": {
                "hdCode": "ac2b-67b01b8d8815",
                "assertCode": "ZC-CS-WATER-0001",
                "cmCode": "12000023000001011",
                "name": "进贤监控点",
                "manu": {
                    "id": "3e7554f5-9e91-4572-9939-e4abf888c55b",
                    "fullName": "广东东鹏"
                },
                "model": {
                    "basic": {
                        "id": "3e7554f5-9e91-4572-9939-e4abf888c55b",
                        "name": "DP-RTU-D4F7"
                    }
                },
                "org": {
                    "id": 22,
                    "fullName": "进贤供水公司"
                }
            },
            "extends": {
                "monitorPoint": {
                    "id": "3e7554f5-9e91-4572-9939-e4abf888c55b",
                    "modelId": "3e7554f5-9e91-4572-9939-e4abf888c55b",
                    "creator": 1,
                    "createTime": "2018-02-09 10:43:45",
                    "operateTime": "2018-03-16 15:37:27",
                    "basic": {
                        "name": "进贤监控点1",
                        "installDate": "2018-02-09 10:43:45",
                        "longitude": "11.111",
                        "latitude": "11.111",
                        "enable": true,
                        "address": "站前东广场",
                        "alarmPhone": "18800000000"
                    }
                }
            }
        }
    ]
}
        """.trimIndent()

        val resp = ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue<WaterMeterResponse>(str)
        lgr.info("parsed: ${resp}\n${ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(resp)}")
    }

    @Test
    fun testLeping() {
        val str = """{"businessKey":"bigmeter","content":{"data":{"forwardFlow":803776,"reverseFlow":2,"reading":-99,"pressure":-99,"valveStatus":-99,"flow":5.519,"voltage":-99},"origin":"1","cmCode":"110040640001"}}"""
        val msg = JSON.parseObject(str, FlowMsg::class.java)
        Assert.notNull(msg.content?.cmCode, "meter-code can't be null")
        Assert.notNull(msg.content?.timestamp, "timestamp can't be null")
        lgr.info("flow msg: {}", ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(msg))
    }

    companion object {
        private val lgr = LoggerFactory.getLogger(ModelTests::class.java)
    }
}