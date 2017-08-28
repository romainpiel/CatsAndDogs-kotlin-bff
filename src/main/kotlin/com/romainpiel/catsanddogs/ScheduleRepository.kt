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

enum class Conference {
    MCE4,
    KotlinConf
}

class ScheduleRepository {
    private val gson: Gson
    private val service: ScheduleService

    init {
        gson = GsonBuilder()
                .registerTypeAdapter(OffsetDateTime::class.java, JsonDeserializer { json, type, context ->
                    OffsetDateTime.parse(json.asString)
                })
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://catsanddogs-swift-server.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        service = retrofit.create(ScheduleService::class.java)
    }

    fun schedule(from: OffsetDateTime?, locale: Locale, conference: Conference): Single<String> {
        val safeFrom = from ?: OffsetDateTime.MIN

        when (conference) {
                Conference.MCE4 -> return scheduleMCE4(safeFrom, locale)
                Conference.KotlinConf -> return scheduleKotlinConf(safeFrom, locale)
        }
    }

    private fun scheduleMCE4(from: OffsetDateTime, locale: Locale): Single<String> {
        return service.getMCE4Schedule()
                .map { it.schedule }
                .flatMapIterable { it }
                .filter { !from.isAfter(it.datestamp) }
                .map {
                    val date = it.datestamp.format(ofPattern("EEEE, d MMMM y", locale)).capitalize()
                    val time = it.datestamp.format(ofPattern("HH:mm", locale))
                    Card(it.title, it.speaker.joinToString(), date, time)
                }
                .toList()
                .toJson(gson)
    }

    private fun scheduleKotlinConf(from: OffsetDateTime, locale: Locale): Single<String> {
        return service.getKotlinConfSchedule()
                .map { it.schedule }
                .flatMapIterable { it }
                .filter { !from.isAfter(it.datestamp) }
                .map {
                    val date = it.datestamp.format(ofPattern("EEEE, d MMMM y", locale)).capitalize()
                    val time = it.datestamp.format(ofPattern("HH:mm", locale))
                    Card(it.title, it.speaker.joinToString(), date, time)
                }
                .toList()
                .toJson(gson)
    }
}