package com.abel.bigwater.jxmsg

/**
 * <pre>
{
    "businessKey":"分发应用业务key",
    "type":"操作类型",
    "content":{
        "cmCode":"dev001",
        "timestamp":12345678910,
        "origin":"1",
        "data":{
            "pressure":1,
            "flow":1
        }
    }
}
 * </pre>
 */
class FlowMsg {
    var businessKey: String? = null

    var type: String? = null

    var content: FlowMsgContent? = null
}

class FlowMsgContent {
    var cmCode: String? = null

    var timestamp: Long? = null

    var origin: String? = null

    var data: FlowMsgContentData? = null
}

class FlowMsgContentData {
    var pressure: Double? = null

    var flow: Double? = null
}