package com.abel.bigwater.jxmsg

import com.abel.bigwater.data.mapper.MeterMapper
import com.abel.bigwater.jxwg.AuthUserResult
import com.abel.bigwater.jxwg.DmaMeterReq
import com.abel.bigwater.jxwg.model.WaterMeterResponse
import com.abel.bigwater.model.zone.ZoneMeter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.http.HttpStatus
import org.apache.http.NameValuePair
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.RequestBuilder
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
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.ArrayList

@Component
@EnableScheduling
class ProfileTask {
    @Autowired
    var meterMapper: MeterMapper? = null

    @Value("\${jx.auth-url}")
    var authUrl: String? = null

    @Value("\${jx.profile-url}")
    var profileUrl: String? = null

    @Value("\${jx.key1}")
    var key1: String? = null

    @Value("\${jx.key2}")
    var key2: String? = null

    @Value("\${jx.key3}")
    var key3: String? = null

    @Value("\${jx.key4}")
    var key4: String? = null

    @Value("\${jx.key5}")
    var key5: String? = null

    @Value("\${jx.key6}")
    var key6: String? = null

    @Value("\${jx.v1}")
    var v1: String? = null

    @Value("\${jx.v2}")
    var v2: String? = null

    @Value("\${jx.v3}")
    var v3: String? = null

    @Value("\${jx.v4}")
    var v4: String? = null

    @Value("\${jx.v5}")
    var v5: String? = null

    @Value("\${jx.v6}")
    var v6: String? = null

    @Value("\${bigmeter.firmCode}")
    var _firmCode: String? = null

    @Value("\${bigmeter.firmId}")
    var _firmId: String? = null

    @Value("\${jxwg.firmName}")
    var _firmName: String? = null

    @Scheduled(fixedRate = 30 * 86400 * 1000L, initialDelay = 30 * 1000L)
    fun retrieveProfileOnce() {
        val auth = authUser()
        if (auth?.token?.isNotBlank() == true) {
            requestSingle(DmaMeterReq(uri = profileUrl,
                    firmId = _firmId,
                    firmName = _firmName,
                    pageNo = 1,
                    pageSize = 10), 0, auth.token!!)
        } else {
            lgr.error("invalid token from auth server: ... ${auth}")
        }
    }

    fun authUser(): AuthUserResult? {
        val post = RequestBuilder.get(authUrl!!)
                .addHeader(key1, v1)
                .addHeader(key2, v2)
                .addHeader(key3, v3)
                .addHeader(key4, v4)
                .addHeader(key5, v5)
                .addHeader(key6, v6)
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("Accept", "application/json;charset=UTF-8")
                .build()
        lgr.info("post: ${post}")

        val hc = HttpClients.createDefault()
        val resp = hc.execute(post).also {
            val text = it.entity.content.reader(Charsets.UTF_8).readText()

            if (it.statusLine.statusCode <= HttpStatus.SC_MULTIPLE_CHOICES) {
                lgr.info("status: ${it.statusLine}\n...${text}")
            } else {
                lgr.info("status failure: ${it.statusLine}\n${text}")
            }

            return ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(text, AuthUserResult::class.java)
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

                val get = RequestBuilder.get("${req.uri}?${URLEncodedUtils.format(params, Charsets.UTF_8)}").apply {
                    addHeader(key1, v1)
                    addHeader(key2, v2)
                    addHeader(key3, v3)
                    addHeader(key4, v4)
                    addHeader(key5, v5)
                    addHeader(key6, v6)
                    addHeader("X-ID-TOKEN", token)
                    addHeader("Content-Type", "application/json;charset=UTF-8")
                    addHeader("Accept", "application/json;charset=UTF-8")
                    addHeader("Accept-Language", "zh-CN, en-US")
                }.build()

                try {
                    val hc = HttpClients.createDefault()
                    lgr.info("About to execute: ${get}")
                    val resp = hc.execute(get)
                    lgr.info("Response: ${resp}")

                    val text = resp.entity.content.bufferedReader(Charsets.UTF_8).readText()
                    lgr.info("profile list: ${text}")
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
            val mid = "${_firmCode}${wm.basic!!.cmCode}"
            val eid = "${_firmCode}${wm.basic!!.cmCode}"
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

    companion object {
        private val lgr = LoggerFactory.getLogger(ProfileTask::class.java)
    }
}