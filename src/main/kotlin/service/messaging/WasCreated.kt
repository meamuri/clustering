package service.messaging

import io.vertx.core.json.JsonObject

class WasCreated(val label: String, val body: String, val ts: Long): ResultMessage(ResultType.Created) {
    override fun toJsonObject(): JsonObject {
        val res = super.toJsonObject()
        res.put("body", body)
        res.put("ts", ts)
        return res
    }
}