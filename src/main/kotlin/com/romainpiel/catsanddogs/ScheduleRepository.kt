package com.romainpiel.catsanddogs

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.romainpiel.catsanddogs.api.ScheduleService
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter.ofPattern
import java.util.*

class ScheduleRepository {
    private val gson: Gson
    private val service: ScheduleService
    private val locale = Locale.forLanguageTag("pl")

    init {
        gson = GsonBuilder()
                .registerTypeAdapter(OffsetDateTime::class.java, JsonDeserializer { json, type, context ->
                    OffsetDateTime.parse(json.asString)
                })
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://catsanddogs-kotlin-server.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        service = retrofit.create(ScheduleService::class.java)
    }

    fun schedule(from: OffsetDateTime?): Single<String> {
        val safeFrom = from ?: OffsetDateTime.MIN
        return service.getConference()
                .map { it.schedule }
                .flatMapIterable { it }
                .filter { !safeFrom.isAfter(it.datestamp) }
                .map {
                    val date = it.datestamp.format(ofPattern("EEEE, d MMMM y", locale)).capitalize()
                    val time = it.datestamp.format(ofPattern("HH:mm", locale))
                    Card(it.title, it.speaker.joinToString(), date, time)
                }
                .toList()
                .toJson(gson)
    }
}