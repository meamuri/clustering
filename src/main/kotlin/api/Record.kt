package api

import java.time.Instant

data class Record(val tid: Int, val body: String, val timestamp: Instant, val parameters: List<String>)