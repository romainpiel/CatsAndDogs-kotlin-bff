package com.romainpiel.catsanddogs

import spark.Spark.*
import java.io.IOException
import java.lang.System.getenv
import java.time.OffsetDateTime

fun main(args: Array<String>) {
    val scheduleRepository = ScheduleRepository()

    getenv("PORT")?.let { port(it.toInt()) }

    exception(IOException::class.java) { exception, request, response ->
        internalServerError("Exception for request: ${request.uri()}\n$exception")
    }

    get("/") { req, res -> "Cats And Dogs - Kotlin - BFF Says Hello" }
    get("/schedule.json") { req, res ->
        val fromStr: String? = req.queryParams("from")
        val from = fromStr?.map { OffsetDateTime.parse(it) }

        scheduleRepository.schedule(from).blockingGet()
    }
}
