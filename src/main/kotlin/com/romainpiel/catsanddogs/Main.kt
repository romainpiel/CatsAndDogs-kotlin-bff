package com.romainpiel.catsanddogs

import okhttp3.OkHttpClient
import okhttp3.Request
import spark.Spark.*
import java.io.IOException
import java.lang.System.getenv


fun main(args: Array<String>) {
    val client = OkHttpClient()

    getenv("PORT")?.let { port(it.toInt()) }

    exception(IOException::class.java) { exception, request, response ->
        internalServerError("Exception for request: ${request.uri()}\n$exception")
    }

    get("/") { req, res -> "Cats And Dogs - Kotlin - Server Says Hello" }
    get("/schedule.json") { req, res ->
        val request = Request.Builder()
                .url("https://catsanddogs-kotlin-server.herokuapp.com/schedule.json")
                .build()

        val response = client.newCall(request).execute()
        response.body().string()
    }
}
