package com.romainpiel.catsanddogs

import com.romainpiel.catsanddogs.Conference.MCE4
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
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
                val conference = MCE4
                val from = call.request.queryParameters.getDate("from")
                val acceptLanguage = call.request.headers["Accept-Language"] ?: "pl-PL"
                val locale = Locale.forLanguageTag(acceptLanguage)

                val json = scheduleRepository.schedule(from, locale, conference).blockingGet()
                call.respondText(json, ContentType.Application.Json)
            }

            get("/{conference}/schedule.json") {
                val conference = Conference.instance(call.parameters["conference"]) ?: MCE4
                val from = call.request.queryParameters.getDate("from")
                val acceptLanguage = call.request.headers["Accept-Language"] ?: "pl-PL"
                val locale = Locale.forLanguageTag(acceptLanguage)

                val json = scheduleRepository.schedule(from, locale, conference).blockingGet()
                call.respondText(json, ContentType.Application.Json)
            }
        }
    }.start(wait = true)
}