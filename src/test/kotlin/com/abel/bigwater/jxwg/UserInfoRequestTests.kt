package com.abel.bigwater.jxwg

import com.alibaba.fastjson.JSON
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [UserInfoRequestTests::class])
class UserInfoRequestTests {
    @Autowired
    var requester: UserInfoRequestBean? = null

    @Test
    fun testFetchUserInfo() {
        ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(javaClass.getResourceAsStream("/auth-user-result.json"), AuthUserResult::class.java).also {
                    lgr.info("${it.userInfo?.realName}: ${it.token}")
                    lgr.info("to json: ${JSON.toJSONString(it, true)}")
                }
    }

    companion object {
        private val lgr = LoggerFactory.getLogger(UserInfoRequestTests::class.java)
    }
}