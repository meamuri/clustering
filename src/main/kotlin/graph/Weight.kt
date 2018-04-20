package graph

import java.time.Instant

class Weight(timestamp: Instant) {
    var cnt = 1
        private set
    var min = timestamp
        private set
    var max = timestamp
        private set
    var delta: Instant = Instant.MIN
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
        recompute()
    }

    fun recompute(other: Weight) {
        cnt += other.cnt
        min = if (other.min < min) other.min else min
        max = if (other.max > max) other.max else max
        recompute()
    }

    private fun recompute() {
        delta = max.minusMillis(min.toEpochMilli())
        dispersion = delta.toEpochMilli() / cnt.toDouble()
    }
}