package graph

import java.time.Instant

class Weight(timestamp: Instant) {
    var cnt = 1
        private set
    var min = timestamp
        private set
    var max = timestamp
        private set
    var delta = Instant.MIN
        private set
    var dispersion = 0.0
        private set

    fun recompute(timestamp: Instant) {
        cnt++
        if (timestamp < min) {
            min = timestamp
        } else if (timestamp > max) {
            max = timestamp
        }
        delta = max.minusMillis(min.toEpochMilli())
        dispersion = delta.toEpochMilli() / cnt.toDouble()
    }
}