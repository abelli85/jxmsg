package com.abel.bigwater.jxwg.model

data class Manu(var id: String? = null, var fullName: String? = null) {
}

data class Model(var basic: ModelBasic? = null) {
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
) {}

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
        var extend: WaterMeterExtend? = null

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