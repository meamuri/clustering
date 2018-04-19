package api

import com.google.gson.annotations.SerializedName
import java.time.Instant

data class Record(
        @SerializedName("tid") val tid: Int,
        @SerializedName("body") val body: String,
        @SerializedName("timestamp") val timestamp: Instant,
        @SerializedName("parameters") val parameters: List<String>)