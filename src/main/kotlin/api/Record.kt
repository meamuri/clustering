package api

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName


data class Record(
        @SerializedName("tid") val tid: Int,
        @SerializedName("body") val body: String,
        @SerializedName("timestamp") val timestamp: Long,
        @SerializedName("parameters") val parameters: List<String>) {

    companion object {
        fun fromJsonString(json: String): Record? {
            return try {
                val res = Gson().fromJson(json, Record::class.java)
                println("receiver: successful received msg")
                res
            } catch (e: RuntimeException) {
                null
            }
        } // fromJsonString
    } // ... companion object

}
