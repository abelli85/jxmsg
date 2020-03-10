package com.abel.bigwater.jxwg

import com.abel.bigwater.data.mapper.MeterMapper
import com.abel.bigwater.jxwg.model.WaterMeterResponse
import com.abel.bigwater.model.zone.ZoneMeter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.http.HttpStatus
import org.apache.http.NameValuePair
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.joda.time.DateTime
import org.joda.time.Duration
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.FileReader
import java.util.*
import kotlin.collections.ArrayList

@Component
class RequestDmaMeterBean {
    companion object {
        const val REQ_JSON = "request.json"

        private val lgr = LoggerFactory.getLogger(RequestDmaMeterBean::class.java)

        var reqList: DmaMeterReqList? = null

        init {
            if (reqList == null) {
                reqList = ObjectMapper().readValue<DmaMeterReqList>(FileReader(REQ_JSON))
            }
            lgr.info("Count of request: ${reqList?.reqList?.size}")
        }
    }

    @Autowired
    var meterMapper: MeterMapper? = null

    @Value("\${jxwg.firmId}")
    var firmId: String? = null

    @Value("\${jxwg.firmCode}")
    var firmCode: String? = null

    @Value("\${jxwg.firmName}")
    var firmName: String? = null

    @Autowired
    var tokenRequest: UserInfoRequestBean? = null

//    @Scheduled(initialDelay = 60 * 1000, fixedDelay = 24 * 3600 * 1000)
    fun requestDmaMeter() {
        lgr.info("About to execute request: ${reqList?.reqList?.size}")
        val authUser = tokenRequest?.fetchUserInfo()

        reqList?.reqList.orEmpty().forEachIndexed { idx, req ->
            requestSingle(req, idx, authUser?.token!!)
        }
    }

    fun requestSingle(req: DmaMeterReq, idx: Int, token: String) {
        var cnt: Int
        var pageNo = 0

        do {
            cnt = 0

            // 未更新; 或者上次更新已超7天.
            if (!req.success || (req.success && Duration(DateTime(req.lastSuccess!!), DateTime.now()).standardDays >= 7)) {
                val params = ArrayList<NameValuePair>()

                if (req.rsql != null) params.add(BasicNameValuePair("rsql", req.rsql))
                params.add(BasicNameValuePair("pageNo", "${++pageNo}"))
                if (req.pageSize != null) params.add(BasicNameValuePair("pageSize", req.pageSize.toString()))

                val get = HttpGet("${req.uri}?${URLEncodedUtils.format(params, Charsets.UTF_8)}")
                get.setHeader("x-id-token", token)
                get.setHeader("Content-Type", "application/json;charset=UTF-8")
                get.setHeader("Accept", "application/json;charset=UTF-8")
                get.setHeader("Accept-Language", "zh-CN, en-US")

                try {
                    val hc = HttpClients.createDefault()
                    lgr.info("About to execute: ${get}")
                    val resp = hc.execute(get)
                    lgr.info("Response: ${resp}")

                    val text = resp.entity.content.bufferedReader(Charsets.UTF_8).readText()
                    if (resp.statusLine.statusCode == HttpStatus.SC_OK) {
                        val wmr = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue<WaterMeterResponse>(text)
                        cnt = wmr.count?.toInt() ?: 0

                        updateWaterMeter(wmr, req)

                        req.success = true
                        req.lastSuccess = Date()
                        lgr.info("Update success[$idx]: ${req.uri}")
                    } else {
                        req.success = false
                        req.lastSuccess = null
                        lgr.warn("Update failed[$idx]: ${req.uri}\n$text")
                    }
                } catch (ex: Exception) {
                    lgr.error("Update failed[$idx]: ${req.uri} caused by ${ex.message}", ex)
                    req.success = false
                    req.lastSuccess = null
                }
            }
        } while (req.pageSize ?: 0 > 0 && cnt >= req.pageSize!!)
    }

    /**
     * 更新DMA水表信息
     */
    internal fun updateWaterMeter(wmr: WaterMeterResponse, req: DmaMeterReq) {
        lgr.info("Update DMA Meter count: ${wmr.count}, ${wmr.resultCode}")

        wmr.queryData?.forEachIndexed { idx, wm ->
            val mid = "${firmCode}${wm.basic!!.cmCode}"
            val eid = "${firmCode}${wm.basic!!.cmCode}"
            lgr.info("Update Meter[$idx:${req.firmName}]: ${mid}-${wm.basic?.name}-${wm.basic?.cmCode} (${wm.extend?.monitorPoint?.basic?.longitude}, ${wm.extend?.monitorPoint?.basic?.latitude})")

            ZoneMeter().apply {
                this.id = mid
                this.meterId = mid
                this.extId = eid
                this.name = wm.basic?.name

                this.userCode = wm.basic?.cmCode
                this.meterCode = wm.basic?.cmCode

                this.meterLoc = GeometryFactory().createPoint(Coordinate(wm.extend?.monitorPoint?.basic?.latitude!!.toDouble(),
                        wm.extend?.monitorPoint?.basic?.longitude!!.toDouble())).toText()

                this.model = wm.modelCode
                this.meterBrandId = wm.basic?.manu?.fullName

                this.firmId = req.firmId
            }.also {
                if (1 > meterMapper!!.updateMeterByValue(it)) {
                    meterMapper!!.insertMeter(it)
                }
            }
        }
    }
}

data class DmaMeterReq(var uri: String? = null,
                       var rsql: String? = null,
                       var firmId: String? = null,
                       var firmName: String? = null,
                       var pageNo: Int? = null,
                       var pageSize: Int? = null) {
    var success: Boolean = false
    var lastSuccess: Date? = null
}

data class DmaMeterReqList(var reqList: ArrayList<DmaMeterReq>? = null) {}