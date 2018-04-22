package service.messaging

import graph.Weight
import io.vertx.core.json.JsonObject

class WasProcessed(val label: String, val prevBody: String, val body: String, val ts: Long, w: Weight)
    : ResultMessage(ResultType.Processed) {
    val min = w.min
    val max = w.max
    val delta = w.delta
    val dispersion = w.dispersion
    override fun toJsonObject(): JsonObject {
        val obj = super.toJsonObject()
        obj.put("label", label)
        obj.put("prevBody", prevBody)
        obj.put("body", body)
        obj.put("ts", ts)
        obj.put("min", min)
        obj.put("max", max)
        obj.put("delta", delta)
        obj.put("dispersion", dispersion)
        return obj
    }
}