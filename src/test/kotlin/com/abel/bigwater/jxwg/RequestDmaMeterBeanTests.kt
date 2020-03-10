package com.abel.bigwater.jxwg

import com.abel.bigwater.jxwg.model.WaterMeterResponse
import com.alibaba.fastjson.JSON
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RequestDmaMeterBeanTests {

    @Autowired
    var reqBean: RequestDmaMeterBean? = null

    @Test
    fun testUpdateMeter() {
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
            "extend": {
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

        val resp = ObjectMapper().readValue<WaterMeterResponse>(str)

        reqBean!!.updateWaterMeter(resp, DmaMeterReq(firmId = "3801", firmName = "测试水司"))
    }

    @Test
    fun testMeterJson() {
        val resp = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(javaClass.getResourceAsStream("/meter-list.json"), WaterMeterResponse::class.java)

        lgr.info("result: ${resp.resultCode},${resp.message?.description}~ count of meter: ${resp.count}\n${resp.queryData?.joinToString(", ", transform = {
            "${it.basic?.cmCode}-${it.basic?.name}(${it.extend?.monitorPoint?.basic?.longitude}, ${it.extend?.monitorPoint?.basic?.latitude})"
        })}")

        lgr.info("to json: ${JSON.toJSONString(resp, true)}")
    }

    @Test
    fun testRequestMeter() {
        reqBean!!.requestDmaMeter()
    }

    @Test
    fun testRequestSingle() {
        reqBean!!.requestSingle(DmaMeterReq("http://59.63.182.52:31012/dma/devices/dev_sub_dma",
                "basic.org.id==95"), 1, "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwYXlsb2FkIjoie1widXNlclR5cGVcIjpcIjFcIixcImFwcElkXCI6XCJlMTdlYzA5ZTJiMzc5YjMzYTEwYzE4MDdiNWY2ZjYxM1wiLFwiYXBwU2VjcmV0XCI6XCI5MmUzYWI5ZjQzYWVjOGJhNDFmNmZiNWMzMjc1Yzc0ZlwiLFwidXNlcklkXCI6MzQyMCxcInVzZXJuYW1lXCI6XCJsaXVqXCIsXCJuYW1lXCI6XCLliJjkvbNcIixcInBhc3N3b3JkXCI6XCIxMjM0NTZcIixcImVudGVySWRcIjoxLFwiZG9tYWluXCI6XCJ3d3cuamlhbmd4aXdhdGVyLmNvbVwiLFwib3JnSWRcIjoyLFwiaXNzdWVUaW1lXCI6MTUzNDgzNjg4NTAzMX0ifQ.SrIeTHZemsepLlHX0jqQfMDKZTTBzlT2J_4YFBa-thk")
    }

    companion object {
        private val lgr = LoggerFactory.getLogger(RequestDmaMeterBeanTests::class.java)
    }
}