package com.abel.bigwater.jxmsg

import lombok.extern.slf4j.Slf4j
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Slf4j
internal class ProfileTaskTest {
    @Autowired
    var profileTask: ProfileTask? = null

    @Test
    fun retrieveProfileOnce() {
        profileTask!!.retrieveProfileOnce()
    }

    companion object {
        private val lgr = LoggerFactory.getLogger(ProfileTaskTest::class.java)
    }
}