package graph


const val ManagerControlMax = 5
const val MetricLevel = 0.3

fun getMetrics(graph: Graph, otherGraph: Graph): Double {
    val size = graph.getNodes().size
    val sizeWithoutCopies = graph.getNodes().dropWhile { it in otherGraph.getNodes() }.size
    return 1 - (sizeWithoutCopies / size.toDouble())
}