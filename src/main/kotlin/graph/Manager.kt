package graph

import api.Record
import com.google.gson.Gson
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future

const val ManagerControlMax = 5
const val MetricLevel = 0.3

class Manager: AbstractVerticle() {
    private val graphs: MutableList<Graph> = mutableListOf()
    private val tidToProcessor: MutableMap<Int, Int> = mutableMapOf()

    val countOfGraphs get() = graphs.size

    override fun start(startFuture: Future<Void>?) {
        val eb = vertx.eventBus()
        eb.consumer<Any>("records-feed", {
            println("hello!")
            val record = Gson().fromJson(it.body().toString(), Record::class.java)
            registerRecord(record)
            println("$record was added to cluster")
        })
    }

    fun registerRecord(record: Record) {
        val processorIndex = tidToProcessor[record.tid]
        if (processorIndex != null) {
            graphs[processorIndex].registerRecord(record)
            return
        }
        if (countOfGraphs == ManagerControlMax) {
            clustering(record)
            return
        }
        graphs.add(Graph(record))
        tidToProcessor[record.tid] = graphs.lastIndex
    }

    fun findTidProcessor(tid: Int): Int = tidToProcessor[tid] ?: -1

    fun getGraphOfTid(tid: Int): Graph? {
        val index = findTidProcessor(tid)
        return if (index == -1) null else graphs[index]
    }

    private fun clustering(record: Record) {
        val srcToDestPair = getMergePair()
        merge(srcToDestPair.first, srcToDestPair.second)
        graphs[srcToDestPair.first] = Graph(record)
        tidToProcessor[record.tid] = srcToDestPair.first
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
}
