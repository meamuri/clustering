package graph

import api.Record
import io.vertx.core.AbstractVerticle
import service.messaging.ResultMessage
import service.messaging.WasClustered
import service.messaging.WasCreated
import service.messaging.WasProcessed
import settings.GraphSaverChannel
import settings.RecordsChannel


class Manager: AbstractVerticle() {
    private val graphs: MutableList<Graph> = mutableListOf()
    private val tidToProcessor: MutableMap<Int, Int> = mutableMapOf()

    val countOfGraphs get() = graphs.size

    override fun start() {
        vertx.eventBus().consumer<String>(RecordsChannel, lbl@{
            val record = Record.fromJsonString(it.body())
            it.reply("ok")
            if (record == null) {
                return@lbl
            } else {
                val res = registerRecord(record)
                vertx.eventBus().send(GraphSaverChannel, res.toJsonObject())
            }
        })

        vertx.setPeriodic(15000, {
            println("graph count: " + graphs.size)
            graphs.forEachIndexed({ index, graph ->
                println("\t * ${index + 1} : graph size -- ${graph.nodes.size}")
            })
        })
    }

    fun registerRecord(record: Record): ResultMessage {
        val processorIndex = tidToProcessor[record.tid]
        if (processorIndex != null) {
            val prevNode = graphs[processorIndex].getCurrentNode().body
            val isLoop = graphs[processorIndex].nodes.containsKey(record.body.hashCode())
            graphs[processorIndex].registerRecord(record)
            val lbl = "g" + processorIndex.toString()
            return WasProcessed(lbl, prevNode, record.body, record.timestamp, Weight(record.timestamp), isLoop)
        }
        if (countOfGraphs == ManagerControlMax) {
            val pair = clustering(record)
            return WasClustered(generateLabel(pair.first), generateLabel(pair.second))
        }
        graphs.add(Graph(record))
        tidToProcessor[record.tid] = graphs.lastIndex
        return WasCreated("g" + graphs.lastIndex.toString(), record.body, record.timestamp)
    }

    fun findTidProcessor(tid: Int): Int = tidToProcessor[tid] ?: -1

    fun getGraphOfTid(tid: Int): Graph? {
        val index = findTidProcessor(tid)
        return if (index == -1) null else graphs[index]
    }

    private fun clustering(record: Record): Pair<Int, Int> {
        val srcToDestPair = getMergePair()
        merge(srcToDestPair.first, srcToDestPair.second)
        graphs[srcToDestPair.first] = Graph(record)
        tidToProcessor[record.tid] = srcToDestPair.first
        return srcToDestPair
    }

    fun getMergePair(): Pair<Int, Int> {
        for (i in 0 until graphs.lastIndex) {
            for (j in i + 1..graphs.lastIndex) {
                if (getMetrics(graphs[i], graphs[j]) > MetricLevel) {
                    return Pair(i, j)
                }
            }
        }
        return Pair(graphs.lastIndex, 0)
    }

    private fun merge(sourceIndex: Int, destinationIndex: Int) {
        val graph = graphs[sourceIndex]
        graphs[destinationIndex].merge(graph)
        tidRelinking(sourceIndex, destinationIndex)
    }

    private fun tidRelinking(fromProcess: Int, toProcess: Int) {
        for ((tid, processPos) in tidToProcessor) {
            if (processPos == fromProcess) {
                tidToProcessor[tid] = toProcess
            }
        }
    }

    private fun generateLabel(param: Int): String = "g" + param.toString()
}
