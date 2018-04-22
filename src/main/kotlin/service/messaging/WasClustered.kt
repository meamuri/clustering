package service.messaging

import io.vertx.core.json.JsonObject

class WasClustered(val labelBefore: String, val labelAfter: String): ResultMessage(ResultType.Clustered) {
    override fun toJsonObject(): JsonObject {
        val obj = super.toJsonObject()
        obj.put("before", labelBefore)
        obj.put("after", labelAfter)
        return obj
    }
}