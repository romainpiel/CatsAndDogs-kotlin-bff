package com.romainpiel.catsanddogs

import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import java.lang.System.getenv
import java.time.OffsetDateTime
fun main(args: Array<String>) {
    val scheduleRepository = ScheduleRepository()

    val port = getenv("PORT")?.toInt() ?: 8080

    embeddedServer(Netty, port) {
        routing {
            // todo: update intelli-j and use _
            get("/") { req, response ->
                call.respondText("Cats And Dogs - Kotlin - BFF Says Hello", ContentType.Text.Html)
            }

            get("/schedule.json") { req, res ->
                val fromStr: String? = call.request.queryParameters["from"]
                val from = fromStr?.let { OffsetDateTime.parse(it) }

                scheduleRepository.schedule(from).blockingGet()
            }
        }
    }.start(wait = true)
}
