package com.abel.bigwater.jxmsg

import com.abel.bigmeter.service.DataParam
import com.abel.bigwater.data.mapper.DataMapper
import com.alibaba.fastjson.JSON
import lombok.extern.slf4j.Slf4j
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Slf4j
internal class FlowSenderTest {

    @Autowired
    var sender: FlowSender? = null

    @Autowired
    var dataMapper: DataMapper? = null

    @Test
    fun sendMsg() {
        sender!!.sendMsg(FlowMsg().apply {
            businessKey = "分发应用业务key"
            type = "操作类型"
            content = FlowMsgContent().apply {
                cmCode = "dev001"
                timestamp = DateTime(2020, 1, 5, 1, 0).millis
                origin = "1"

                data = FlowMsgContentData().apply {
                    pressure = 1.2
                    flow = 3.4
                }
            }
        }, "bigMeter")

        lgr.info("send message to bigMeter.")
    }

    @Test
    fun sendRealMsg1() {
        val msg = """{"businessKey":"bigMeter","content":{"data":{"forwardFlow":68997,"reverseFlow":-99,"reading":-99,"pressure":0.145,"valveStatus":-99,"flow":10.527,"voltage":-99},"origin":"1","cmCode":"110040590001","timestamp":1581909300000}}"""
        lgr.info("about to send msg1: {}", msg)
        sender!!.kafkaTemp!!.send("bigMeter", msg)
        lgr.info("sent real msg1")
    }

    @Test
    fun testDataMapper() {
        val lst = dataMapper!!.selectRealtime(DataParam(meterId = "jx-dev001", extId = "jx-dev001"))

        lgr.info("list of realtime: {}", JSON.toJSONString(lst, true))
    }

    companion object {
        private val lgr = LoggerFactory.getLogger(FlowSenderTest::class.java)
    }
}