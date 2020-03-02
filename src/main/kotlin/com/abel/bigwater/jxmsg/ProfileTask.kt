package com.abel.bigwater.jxmsg

import com.abel.bigwater.data.mapper.MeterMapper
import org.apache.http.HttpStatus
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.impl.client.HttpClients
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class ProfileTask {
    @Autowired
    var meterMapper: MeterMapper? = null

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

    @Scheduled(fixedRate = 30 * 86400 * 1000L, initialDelay = 20 * 1000L)
    fun retrieveProfileOnce() {
        val post = RequestBuilder.post(profileUrl!!)
                .addHeader(key1, v1)
                .addHeader(key2, v2)
                .addHeader(key3, v3)
                .addHeader(key4, v4)
                .addHeader(key5, v5)
                .addHeader(key6, v6)
                .build()
        lgr.info("post: ${post}")

        val hc = HttpClients.createDefault()
        val resp = hc.execute(post)
        lgr.info("profile request result: ${resp.statusLine}")
        if (resp.statusLine.statusCode <= HttpStatus.SC_MULTIPLE_CHOICES) {
            val body = resp.entity.content.reader(Charsets.UTF_8).readText()
            lgr.info("profile body: ${body}")
        }
    }

    companion object {
        private val lgr = LoggerFactory.getLogger(ProfileTask::class.java)
    }
}