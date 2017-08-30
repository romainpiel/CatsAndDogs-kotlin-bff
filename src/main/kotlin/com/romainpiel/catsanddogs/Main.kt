package com.romainpiel.catsanddogs

import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.header
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
                val from = offsetDateTime(call.request.queryParameters)
                val conference = Conference.MCE4
                val acceptLanguage = "pl-PL"
                val locale = Locale.forLanguageTag(acceptLanguage)

                val json = scheduleRepository.schedule(from, locale, conference).blockingGet()
                call.respondText(json, ContentType.Application.Json)
            }

            get("/{conference}/schedule.json") {
                val from = offsetDateTime(call.request.queryParameters)
                val conference: Conference = Conference.instance(call.parameters["conference"]) ?: Conference.MCE4
                val acceptLanguage: String = call.request.headers["Accept-Language"] ?: "pl-PL"
                val locale = Locale.forLanguageTag(acceptLanguage)

                val json = scheduleRepository.schedule(from, locale, conference).blockingGet()

                call.response.header("A", locale.toString())
                call.respondText(json, ContentType.Application.Json)
            }
        }
    }.start(wait = true)
}

fun offsetDateTime(queryParameters: ValuesMap): OffsetDateTime {
    val fromStr: String? = queryParameters["from"]
    val from = fromStr?.let { OffsetDateTime.parse(it) }
    val safeFrom = from ?: OffsetDateTime.MIN

    return safeFrom
}
