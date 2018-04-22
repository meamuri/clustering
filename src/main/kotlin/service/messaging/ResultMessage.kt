package service.messaging

import io.vertx.core.json.JsonObject

abstract class ResultMessage(private val resultType: ResultType) {
    open fun toJsonObject(): JsonObject {
        val obj = JsonObject()
        obj.put("resultType", this.resultType)
        return obj
    }
}
