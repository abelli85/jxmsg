package com.abel.bigwater.jxmsg

import lombok.extern.slf4j.Slf4j
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
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
