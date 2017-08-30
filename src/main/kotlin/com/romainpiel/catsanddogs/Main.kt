package com.romainpiel.catsanddogs

import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import org.jetbrains.ktor.util.*

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

            // deprecated
            get("/schedule.json") {
                val parameters = parameters(call.request.queryParameters)
                val conference = Conference.MCE4

                val json = scheduleRepository.schedule(parameters.from, parameters.locale, conference).blockingGet()
                call.respondText(json, ContentType.Application.Json)
            }

            get("/{conference}/schedule.json") {
                val parameters = parameters(call.request.queryParameters)
                val conference: Conference = Conference.instance(call.parameters["conference"]) ?: Conference.MCE4

                val json = scheduleRepository.schedule(parameters.from, parameters.locale, conference).blockingGet()
                call.respondText(json, ContentType.Application.Json)
            }
        }
    }.start(wait = true)
}

data class BFFParameters(val from: OffsetDateTime,
                         val locale: Locale)

fun parameters(queryParameters: ValuesMap): BFFParameters {
    val fromStr: String? = queryParameters["from"]
    val from = fromStr?.let { OffsetDateTime.parse(it) }
    val safeFrom = from ?: OffsetDateTime.MIN

    // TODO: Locale
    val locale = Locale.forLanguageTag("pl")

    return BFFParameters(safeFrom, locale)
}
