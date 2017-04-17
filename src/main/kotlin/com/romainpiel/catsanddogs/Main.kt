package com.romainpiel.catsanddogs

import com.google.gson.Gson
import spark.Spark.*
import java.io.IOException
import java.lang.System.getenv


fun main(args: Array<String>) {
    val scheduleRepository = ScheduleRepository()
    val gson = Gson()

    getenv("PORT")?.let { port(it.toInt()) }

    exception(IOException::class.java) { exception, request, response ->
        internalServerError("Exception for request: ${request.uri()}\n$exception")
    }

    get("/") { req, res -> "Cats And Dogs - Kotlin - Server Says Hello" }
    get("/schedule.json") { req, res ->
        scheduleRepository.schedule()
                .toJson(gson)
                .blockingGet()
    }
}
