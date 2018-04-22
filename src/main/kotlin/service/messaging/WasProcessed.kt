package service.messaging

import graph.Weight
import io.vertx.core.json.JsonObject

class WasProcessed(val label: String, val prevBody: String, val body: String, val ts: Long, val w: Weight)
    : ResultMessage(ResultType.Processed) {
    override fun toJsonObject(): JsonObject {
        val obj = super.toJsonObject()
        return obj
    }
}