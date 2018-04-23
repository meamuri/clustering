package helpers

import api.Record
import graph.Graph
import graph.Node
import graph.Weight
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject


fun Weight.toJsonObject(): JsonObject {
    val res = JsonObject()
    res.put("min", this.min)
    res.put("max", this.max)
    res.put("delta", this.delta)
    res.put("dispersion", this.dispersion)
    return res
}

fun Record.toJsonObject(): JsonObject {
    val res = JsonObject()
    res.put("tid", this.tid)
    res.put("body", this.body)
    res.put("timestamp", this.timestamp)
    res.put("parameters", JsonArray(this.parameters))
    return res
}


fun Node.edgesToJsonObject(from: String, graph: Graph): JsonArray = JsonArray(this.relations.map({
        val obj = JsonObject()
        obj.put("from", from)
        obj.put("to", graph.nodes[it.key]!!.body)
        obj.put("weight", it.value.toJsonObject())
    obj
    }))

fun Graph.toJsonObject(): JsonObject {
    val obj = JsonObject()
    obj.put("nodes", JsonArray(this.nodes.map { it.value.body }) )
    val edges = JsonArray()
    this.nodes.forEach({
        edges.addAll(it.value.edgesToJsonObject(it.value.body, this))
    })
    obj.put("edges", edges)
    return obj
}