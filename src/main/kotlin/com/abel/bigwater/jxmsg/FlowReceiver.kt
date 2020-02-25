package com.abel.bigwater.jxmsg

import com.abel.bigwater.data.mapper.DataMapper
import com.abel.bigwater.data.mapper.RtuMapper
import com.abel.bigwater.model.BwData
import com.alibaba.fastjson.JSON
import lombok.extern.slf4j.Slf4j
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.*

@Component
@Slf4j
class FlowReceiver {
    companion object {
        private val lgr = LoggerFactory.getLogger(FlowReceiver::class.java)
    }

    @Autowired
    var dataMapper: DataMapper? = null

    @Autowired
    var rtuMapper: RtuMapper? = null

    @Value("\${bigmeter.firmCode}")
    var _firmCode: String? = null

    @Value("\${bigmeter.firmId}")
    var _firmId: String? = null

    @KafkaListener(topics = ["bigMeter"])
    fun listen(record: ConsumerRecord<Any, Any>) {
        val opt = Optional.ofNullable(record.value())

        if (opt.isPresent) {
            val json = opt.get()
            lgr.info("got message: {}", json)

            try {
                val msg = JSON.parseObject(json.toString(), FlowMsg::class.java)
                lgr.info("parsed as msg: {}", msg)

                val data = BwData().apply {
                    firmId = _firmId
                    extId = _firmCode + msg.content?.cmCode
                    sampleTime = DateTime(msg.content?.timestamp!!).toDate()
                    literPulse = 1000
                    forwardDigits = msg.content?.data?.forwardFlow
                    revertDigits = msg.content?.data?.reverseFlow?.times(-1.0)

                    avgFlow = msg.content?.data?.flow
                    pressure = msg.content?.data?.pressure
                    pressureDigits = msg.content?.data?.pressure
                }

                val cnt = dataMapper!!.updateRealtimeByValue(data)
                if (cnt < 1) {
                    dataMapper!!.insertRealtime(data)
                }
                lgr.info("persist $cnt row: {}", JSON.toJSONString(data))
            } catch (ex: Exception) {
                lgr.error("fail to parse msg caused by {}: {}", ex.message, json)
                lgr.trace(ex.message, ex)
            }
        }
    }
}