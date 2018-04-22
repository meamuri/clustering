package service.messaging

import io.vertx.core.json.JsonObject

class WasClustered: ResultMessage(ResultType.Clustered) {
    override fun toJsonObject(): JsonObject {
        val obj = super.toJsonObject()
        return obj
    }
}