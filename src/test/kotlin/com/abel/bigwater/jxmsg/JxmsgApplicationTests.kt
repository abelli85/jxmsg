package com.abel.bigwater.jxmsg

import lombok.extern.slf4j.Slf4j
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Slf4j
class JxmsgApplicationTests {

	@Test
	fun contextLoads() {
		lgr.info("dummy test.")
	}

	companion object {
		private val lgr = LoggerFactory.getLogger(JxmsgApplicationTests::class.java)
	}
}
