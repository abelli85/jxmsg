package com.abel.bigwater.jxwg.model

data class Manu(var id: String? = null, var fullName: String? = null) {
}

data class Model(var basic: ModelBasic? = null,
                 var id: String? = null,
                 var modelId: String? = null,
                 var active: Boolean? = null) {
}

data class ModelBasic(var id: String? = null,
                      var name: String? = null) {
}

data class Org(var id: String? = null, var fullName: String? = null) {
}

data class MonitorPointBasic(
        var name: String? = null,
        var installDate: String? = null,
        var longitude: String? = null,
        var latitude: String? = null,
        var enable: Boolean? = null,
        var address: String? = null,
        var alarmPhone: String? = null
) {}

data class MonitorPoint(
        var id: String? = null,
        var modelId: String? = null,
        var modelCode: String? = null,
        var creator: String? = null,
        var createTime: String? = null,
        var modifier: String? = null,
        var modifyTime: String? = null,
        var operateTime: String? = null,
        var active: Boolean? = null,
        var basic: MonitorPointBasic? = null
) {
}

/**
 * {
"resultCode": "0050000",
"message": {
"description": "查询成功"
},
"count": 1,
"queryData": [
{
"tag": [
"business_dma_rtu",
"dma_tag_dev_rtu",
"dianzi",
"dianxin"
],
"basic": {
"org": {
"id": 36,
"fullName": "江西省乐平润泉供水有限公司"
},
"manu": {
"id": 909,
"fullName": "浙江和达科技股份有限公司"
},
"name": "乐平宝乐迪监测点RTU",
"model": {
"basic": {
"code": "DEV-RTU-01_HD",
"name": "和达RTU型号"
},
"connInfo": {},
"id": "6d1d3138-21cb-4fb8-aa3d-14d81a3e3e32",
"modelId": "MODEL-DEV-RTU-01",
"creator": 4,
"creatorInfo": {},
"createTime": "2018-05-10 20:38:15",
"operateTime": "2018-05-10 20:38:15",
"active": true
},
"cmCode": "11004055",
"channelNO": "11004055"
},
"extend": {
"cardcode": "144016577019"
},
"relation": [
{
"device": {
"id": "c140cc9b-2295-4223-bf6a-1881e5e2347b",
"refId": "53e6a0c3-9bf8-425c-997c-40e9726ecb76"
},
"channelNO": "P1",
"metricName": [
"forwardFlow"
]
},
{
"device": {
"id": "c140cc9b-2295-4223-bf6a-1881e5e2347b"
},
"channelNO": "A4",
"metricName": [
"pressure"
]
},
{
"device": {
"id": "c140cc9b-2295-4223-bf6a-1881e5e2347b"
},
"channelNO": "A7",
"metricName": [
"flow"
]
}
],
"id": "e0ad3212-f1fe-4cf7-9a8e-04a25dcd785e",
"modelId": "DEV-RTU-01",
"modelCode": "DEV-RTU-01",
"creator": 3830,
"creatorInfo": {
"id": 3830,
"name": "石铁柱"
},
"createTime": "2018-11-22 17:08:55",
"operateTime": "2020-01-06 16:39:04",
"modifier": 3830,
"modifierInfo": {
"id": 3830,
"name": "石铁柱"
},
"modifyTime": "2020-01-06 16:39:04",
"active": true
}
]
}
 */
data class WaterMeter(
        var id: String? = null,
        var modelId: String? = null,
        var modelCode: String? = null,
        var creator: String? = null,
        var createTime: String? = null,
        var modifier: String? = null,
        var modifyTime: String? = null,
        var operateTime: String? = null,
        var active: Boolean? = null,
        var basic: WaterMeterBasic? = null,
        var extend: WaterMeterExtend? = null,

        var relation: ArrayList<RtuChannel>? = null,

        var creatorInfo: CreatorInfoType? = null,
        var modifierInfo: CreatorInfoType? = null
) {}

data class WaterMeterBasic(
        var name: String? = null,
        var hdCode: String? = null,
        var org: Org? = null,
        var manu: Manu? = null,
        var model: Model? = null,
        var cmCode: String? = null,
        var enable: Boolean? = null,
        var assertCode: String? = null
) {}

data class WaterMeterExtend(var monitorPoint: MonitorPoint? = null) {}

data class Message(
        var description: String? = null,
        var checkErrors: ArrayList<CheckError>? = null
) {}

data class CheckError(
        var errorColum: String? = null,
        var errorDetail: String? = null
) {}

data class WaterMeterResponse(
        var resultCode: String? = null,
        var message: Message? = null,
        var count: Long? = null,
        var queryData: ArrayList<WaterMeter>? = null
) {}

data class CreatorInfoType(
        var id: Int? = null,
        var name: String? = null
)

data class RtuChannel(
        var device: RelationDevice? = null,
        var channelNO: String? = null,
        var metricName: ArrayList<String>? = null)

data class RelationDevice(
        var id: String? = null,
        var refId: String? = null
)