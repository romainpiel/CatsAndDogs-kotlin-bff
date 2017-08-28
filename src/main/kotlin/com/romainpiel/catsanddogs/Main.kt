package com.romainpiel.catsanddogs

import spark.Spark.*
import java.io.IOException
import java.lang.System.getenv
import java.time.OffsetDateTime
import java.util.*

fun main(args: Array<String>) {
    val scheduleRepository = ScheduleRepository()

    getenv("PORT")?.let { port(it.toInt()) }

    exception(IOException::class.java) { exception, request, response ->
        internalServerError("Exception for request: ${request.uri()}\n$exception")
    }

    get("/") { req, res -> "Cats And Dogs - Kotlin - BFF Says Hello" }

    // todo: pass in locale or default

    // deprecated
    get("/schedule.json") { req, res ->
        val fromStr: String? = req.queryParams("from")
        val from = if (fromStr != null) { OffsetDateTime.parse(fromStr) } else { null }
        val locale = Locale.forLanguageTag("pl")

        scheduleRepository.schedule(from, locale, Conference.MCE4).blockingGet()
    }

    get("/mce4/schedule.json") { req, res ->
        val fromStr: String? = req.queryParams("from")
        val from = if (fromStr != null) { OffsetDateTime.parse(fromStr) } else { null }
        val locale = Locale.forLanguageTag("pl")

        scheduleRepository.schedule(from, locale, Conference.MCE4).blockingGet()
    }

    get("/kotlinconf/schedule.json") { req, res ->
        val fromStr: String? = req.queryParams("from")
        val from = if (fromStr != null) { OffsetDateTime.parse(fromStr) } else { null }
        val locale = Locale.forLanguageTag("us")

        scheduleRepository.schedule(from, locale, Conference.KotlinConf).blockingGet()
    }
}
