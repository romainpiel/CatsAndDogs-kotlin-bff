package com.romainpiel.catsanddogs

import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import java.lang.System.getenv
import java.time.OffsetDateTime
import java.util.*

fun main(args: Array<String>) {
    val scheduleRepository = ScheduleRepository()

    val port = getenv("PORT")?.toInt() ?: 8080

    embeddedServer(Netty, port) {
        routing {
            get("/") {
                call.respondText("Cats And Dogs - Kotlin - BFF Says Hello", ContentType.Text.Html)
            }

            get("/schedule.json") {
                val fromStr: String? = call.request.queryParameters["from"]
                val from = fromStr?.let { OffsetDateTime.parse(it) }

                val json = scheduleRepository.schedule(from).blockingGet()
                call.respondText(json, ContentType.Application.Json)
            }
        }
    }.start(wait = true)

//    // todo: get rid of duplicated code
//    // todo: pass in locale or default
//
//    get("/schedule.json") { req, res ->
//        // deprecated
//        val fromStr: String? = req.queryParams("from")
//        val from = if (fromStr != null) {
//            OffsetDateTime.parse(fromStr)
//        } else {
//            null
//        }
//        val locale = Locale.forLanguageTag("pl")
//
//        scheduleRepository.schedule(from, locale, Conference.MCE4).blockingGet()
//    }
//
//    get("/mce4/schedule.json") { req, res ->
//        val fromStr: String? = req.queryParams("from")
//        val from = if (fromStr != null) {
//            OffsetDateTime.parse(fromStr)
//        } else {
//            null
//        }
//        val locale = Locale.forLanguageTag("pl")
//
//        scheduleRepository.schedule(from, locale, Conference.MCE4).blockingGet()
//    }
//
//    get("/kotlinconf/schedule.json") { req, res ->
//        val fromStr: String? = req.queryParams("from")
//        val from = if (fromStr != null) {
//            OffsetDateTime.parse(fromStr)
//        } else {
//            null
//        }
//        val locale = Locale.forLanguageTag("us")
//
//        scheduleRepository.schedule(from, locale, Conference.KotlinConf).blockingGet()
//    }
}
