package graph

import api.Record


// https://currentmillis.com/
const val dayOfLife: Long = 1523739600000                   // 15 april 2018
const val dayInMilliseconds: Long = 86400000                // 24 hours


const val TID = 1
const val SQL_QUERY = "SELECT ? FROM ?"
const val NEO4J_QUERY = "CREATE (?:? {? , ?}) )"
const val NEO4J_MATCH = "MATCH (n:?)-[:?]->(friends) return n"
const val NEO4J_RELATION = "CREATE (?)-[:?]->(ir),(?)-[:?]->(?)"

private fun getRecord(tid: Int, body: String, dayShift: Int): Record =
        Record(tid, body, dayOfLife + dayInMilliseconds * dayShift, listOf("does", "not", "matter"))

fun getRecord(body: String, dayShift: Int = 0): Record = getRecord(TID, body, dayShift)


fun getRecordGenerator(): (String) -> Record {
    var day = -1
    return { v ->
        day++
        getRecord(v, day)
    }
}

fun getTidSpecificGenerator(): (Int, String) -> Record {
    var day = -1
    return { tid, body ->
        day++
        getRecord(tid, body, day)
    }
}