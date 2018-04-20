package api

import com.google.gson.Gson

fun jsonToRecord(json: String): Record? {
    return try {
        val res = Gson().fromJson(json, Record::class.java)
        println("receiver: successful received msg")
        res
    } catch (e: RuntimeException) {
        null
    }
} // fun jsonToRecord
