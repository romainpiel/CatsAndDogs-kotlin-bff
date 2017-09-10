package com.romainpiel.catsanddogs.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer

import com.romainpiel.catsanddogs.api.ScheduleService
import com.romainpiel.catsanddogs.model.*

import io.reactivex.Single

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter.ofPattern
import java.util.*

class ScheduleRepository {
    private val gson: Gson
    private val service: ScheduleService

    init {
        gson = GsonBuilder().registerTypeAdapter(OffsetDateTime::class.java, JsonDeserializer { json, _, _ ->
            OffsetDateTime.parse(json.asString)
        }).create()

        service = ScheduleService.create(gson)
    }

    private fun scheduleList(from: OffsetDateTime, locale: Locale, conference: Conference): Single<List<Item>> {
        return service.getSchedule(locale.getLanguage(), conference.rawValue)
                .map { it.schedule }
                .flatMapIterable { it }
                .filter { !from.isAfter(it.datestamp) }
                .map {
                    val date = it.datestamp.format(ofPattern("EEEE, d MMMM y", locale)).capitalize()
                    val time = it.datestamp.format(ofPattern("HH:mm", locale))
                    Item(it.title, it.speaker.joinToString(), date, time)
                }
                .toList()
    }

    fun schedule(from: OffsetDateTime, locale: Locale, conference: Conference): Single<String> =
            this.scheduleList(from, locale, conference).toJson(gson)

    fun scheduleHtml(from: OffsetDateTime, locale: Locale, conference: Conference) =
            this.scheduleList(from, locale, conference).toHtml()
}
