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
                val locale = Locale.forLanguageTag("pl")
                val conferenceValue: String? = call.request.queryParameters["conference"] ?? "mce4"

                val json = scheduleRepository.schedule(from, locale, Conference.MCE4).blockingGet()
                call.respondText(json, ContentType.Application.Json)
            }

            get("/mce4/schedule.json") {
                val fromStr: String? = call.request.queryParameters["from"]
                val from = fromStr?.let { OffsetDateTime.parse(it) }
                val locale = Locale.forLanguageTag("pl")

                val json = scheduleRepository.schedule(from, locale, Conference.MCE4).blockingGet()
                call.respondText(json, ContentType.Application.Json)
            }

            get("/kotlinconf/schedule.json") {
                val fromStr: String? = call.request.queryParameters["from"]
                val from = fromStr?.let { OffsetDateTime.parse(it) }
                val locale = Locale.forLanguageTag("us")

                val json = scheduleRepository.schedule(from, locale, Conference.KotlinConf).blockingGet()
                call.respondText(json, ContentType.Application.Json)
            }

        }
    }.start(wait = true)
}

private fun parameters(call): (from: String, locale: String, conference: Conference) {
    val fromStr: String? = call.request.queryParameters["from"]
    val from = fromStr?.let { OffsetDateTime.parse(it) }
    val locale = Locale.forLanguageTag("pl")
    val conferenceValue: String? = call.request.queryParameters["conference"] ?? "mce4"
    val conference = Conference(rawValue: conferenceValue)

    return (from, locale, conference)
}
