package graph

class Weight(timestamp: Long) {
    var cnt = 1
        private set
    var min: Long = timestamp
        private set
    var max: Long = timestamp
        private set
    var delta: Long = 0
        private set
    var dispersion = 0.0
        private set

    fun recompute(timestamp: Long) {
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
        delta = max - min
        dispersion = delta / cnt.toDouble()
    }
}