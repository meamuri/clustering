package api

import com.google.gson.annotations.SerializedName


data class Record(
        @SerializedName("tid") val tid: Int,
        @SerializedName("body") val body: String,
        @SerializedName("timestamp") val timestamp: Long,
        @SerializedName("parameters") val parameters: List<String>)
