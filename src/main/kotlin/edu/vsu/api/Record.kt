package edu.vsu.api

data class Record(val tid: Long,
                  val body: String,
                  val timestamp: Long,
                  val params: Map<String, Any>)
