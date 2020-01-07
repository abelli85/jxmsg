package com.abel.bigwater.jxmsg

import com.abel.bigmeter.service.DataParam
import com.abel.bigwater.data.mapper.DataMapper
import com.alibaba.fastjson.JSON
import org.joda.time.DateTime
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
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
                timestamp = DateTime(2020, 1, 5, 0, 0).millis
                origin = "1"

                data = FlowMsgContentData().apply {
                    pressure = 1.2
                    flow = 3.4
                }
            }
        }, "bigMeter")
    }

    @Test
    fun testDataMapper() {
        val lst = dataMapper?.selectRealtime(DataParam(meterId = "jx-xs001", extId = "jx-xs001"))

        lgr.info("list of realtime: {}", JSON.toJSONString(lst, true))
    }

    companion object {
        private val lgr = LoggerFactory.getLogger(FlowSenderTest::class.java)
    }
}