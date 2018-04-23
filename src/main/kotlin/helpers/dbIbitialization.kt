package helpers

import api.Record
import io.vertx.core.eventbus.EventBus
import settings.RecordsChannel

fun timeGenerator(): () -> Long {
    var time: Long = 1523739600000
    return {
        time += 86400000
        time
    }
}

val gen = timeGenerator()

private val records = listOf(
        Record(1, "SELECT * FROM ", gen(), listOf()),
        Record(1, "query {repository(owner: \"user\") {name. description} }", gen(), listOf()),
        Record(1, "MATCH (f:?) where f.body=?", gen(), listOf()),

        Record(2, "MATCH (f:?) where f.body=?", gen(), listOf()),
        Record(2, "SELECT * FROM t", gen(), listOf()),
        Record(2, "db.collection.find({id: ??}.toArray()", gen(), listOf()),

        Record(3, "SELECT id FROM t where id > ?", gen(), listOf()),
        Record(3, "query {repository(owner: \"user\") {name. description} }", gen(), listOf()),
        Record(3, "db.collection.find({id: ??})", gen(), listOf()),
        Record(3, "db.collection.find({id: ??}, {_id: 0, description: 1})", gen(), listOf()),

        Record(4, "db.collection.find({id: ??}.toArray()", gen(), listOf()),
        Record(4, "db.collection.find({id: ??}.limit(?)", gen(), listOf()),

        Record(4, "MATCH (f:?) where f.body=?", gen(), listOf())

)

fun initDatabase(eventBus: EventBus) {
    records.forEach({
        eventBus.send(RecordsChannel, it.toJsonObject().toString())
    })
}