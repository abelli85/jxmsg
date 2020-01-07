package com.abel.bigwater.jxmsg

import com.alibaba.fastjson.JSON
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
@Slf4j
class FlowSender {
    companion object {
        private val lgr = LoggerFactory.getLogger(FlowSender::class.java)
    }

    @Autowired
    var kafkaTemp: KafkaTemplate<String, String>? = null

    /**
     * send msg to specific topic.
     */
    fun sendMsg(msg: FlowMsg, topic: String): Boolean {
        val json = JSON.toJSONString(msg)
        lgr.info("about to send to {}: {}", topic, json)
        return kafkaTemp!!.send(topic, json).isDone
    }
}