package com.abel.bigwater.jxwg

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.FileReader

@Component
class UserInfoRequestBean {

    fun fetchUserInfo(): AuthUserResult {
        val config = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(FileReader("user-req.json"), UserInfoConfig::class.java)

        return fetchUserInfo(config)!!
    }

    fun fetchUserInfo(req: UserInfoConfig): AuthUserResult? {
        val get = HttpGet(req.uri)
        get.setHeader("X-Auth-User", req.authUser)
        get.setHeader("X-Auth-Token", req.authToken)
        get.setHeader("X-Enterprise-Id", req.enterpriseId)
        get.setHeader("X-Auth-User-Type", "${req.userType}")
        get.setHeader("X-App-Id", req.appId)
        get.setHeader("X-App-Secret", req.appSecret)

        get.setHeader("Content-Type", "application/json;charset=UTF-8")
        get.setHeader("Accept", "application/json;charset=UTF-8")

        lgr.info("About to auth user: ${get}")
        val hc = HttpClients.createDefault()
        hc.execute(get).also {
            val text = it.entity.content.reader(Charsets.UTF_8).readText()

            if (it.statusLine.statusCode == HttpStatus.SC_OK) {
                lgr.info("status: ${it.statusLine}\n...${text.takeLast(300)}")
            } else {
                lgr.info("status failure: ${it.statusLine}\n${text}")
            }

            return ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(text, AuthUserResult::class.java)
        }

        return null
    }

    companion object {
        private val lgr = LoggerFactory.getLogger(UserInfoRequestBean::class.java)
    }
}

data class UserInfoConfig(var uri: String? = null,
                          var authUser: String? = null,
                          var authToken: String? = null,
                          var userType: Int? = null,
                          var appId: String? = null,
                          var appSecret: String? = null,
                          var enterpriseId: String? = null,
                          var server: String? = null,
                          var port: Int? = null)

data class AuthUserResult(var resultCode: String? = null,
                          var userInfo: UserInfo? = null,
                          var roleInfos: Array<AdminBindRoleInfo>? = null,
                          var token: String? = null)

data class UserInfo(var id: Int? = null,
                    var name: String? = null,
                    var realName: String? = null,
                    var tel: String? = null,
                    var status: String? = null,
                    var modifyTime: String? = null,
                    var operateTime: Long? = null,
                    var sex: String? = null,
                    var email: String? = null,
                    var token: String? = null,
                    var telphone: String? = null) {}

data class AdminBindRoleInfo(var roleId: Int? = null,
                             var roleName: String? = null,
                             var orgId: Int? = null,
                             var permissionList: Array<AdministratorPermission>? = null,
                             var bindOrgList: Array<Organization>? = null,
                             var bindAllOrgList: Array<Organization>? = null,
                             var parentPathOrgList: Array<Organization>? = null)

data class AdministratorPermission(var id: Int? = null,
                                   var appId: Int? = null,
                                   var parentId: Int? = null,
                                   var name: String? = null,
                                   var permission: String? = null,
                                   var remarks: String? = null,
                                   var option: String? = null)

data class Organization(var id: Int? = null,
                        var orgCode: String? = null,
                        var fullName: String? = null)