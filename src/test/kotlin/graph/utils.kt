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


fun getRecord(body: String, dayShift: Int = 0): Record =
        Record(TID, body, dayOfLife + dayInMilliseconds * dayShift, listOf("does", "not", "matter"))

fun getRecordGenerator(): (String) -> Record {
    var day = -1
    return { v ->
        day++
        getRecord(v, day)
    }
}
