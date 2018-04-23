package service.messaging

import io.vertx.core.json.JsonObject

class WasClustered(val labelBefore: String, val labelAfter: String, val body: String, val ts: Long):
        ResultMessage(ResultType.Clustered) {
    override fun toJsonObject(): JsonObject {
        val obj = super.toJsonObject()
        obj.put("labelBefore", labelBefore)
        obj.put("labelAfter", labelAfter)
        obj.put("body", body)
        obj.put("ts", ts)
        return obj
    }
}