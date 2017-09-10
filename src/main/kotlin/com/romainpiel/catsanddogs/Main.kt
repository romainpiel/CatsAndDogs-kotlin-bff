package com.romainpiel.catsanddogs

import org.jetbrains.ktor.application.*
import org.jetbrains.ktor.host.*
import org.jetbrains.ktor.http.*
import org.jetbrains.ktor.netty.*
import org.jetbrains.ktor.response.*
import org.jetbrains.ktor.routing.*
import org.jetbrains.ktor.util.*
import java.lang.System.getenv
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
                val conference = Conference.MCE4
                val from = call.request.queryParameters.getDate("from")
                val acceptLanguage = call.request.headers["Accept-Language"] ?: "pl-PL"
                val locale = Locale.forLanguageTag(acceptLanguage)

                val json = scheduleRepository.schedule(from, locale, conference).blockingGet()
                call.respondText(json, ContentType.Application.Json)
            }

            get("/{conference}/schedule.json") {
                val conference = Conference.instance(call.parameters["conference"]) ?: Conference.MCE4
                val from = call.request.queryParameters.getDate("from")
                val acceptLanguage = call.request.headers["Accept-Language"] ?: "pl-PL"
                val locale = Locale.forLanguageTag(acceptLanguage)

                val json = scheduleRepository.schedule(from, locale, conference).blockingGet()
                call.respondText(json, ContentType.Application.Json)
            }

            get("/{conference}/schedule.html") {
                val from = offsetDateTime(call.request.queryParameters)
                val conference: Conference = Conference.instance(call.parameters["conference"]) ?: Conference.MCE4
                val acceptLanguage: String = call.request.headers["Accept-Language"] ?: "pl-PL"
                val locale = Locale.forLanguageTag(acceptLanguage)

                val html = scheduleRepository.scheduleHtml(from, locale, conference).blockingGet()

                call.respondText(html, ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}
