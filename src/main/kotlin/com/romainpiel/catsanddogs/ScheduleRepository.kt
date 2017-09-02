package com.romainpiel.catsanddogs

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.romainpiel.catsanddogs.api.ScheduleService
import io.reactivex.Single
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter.ofPattern
import java.util.*

enum class Conference(val rawValue: String) {
    MCE4("mce4"),
    KotlinConf("kotlinconf");

    companion object {
        fun instance(rawValue: String?) = Conference.values().firstOrNull { it.rawValue == rawValue }
    }
}

class ScheduleRepository {
    private val gson: Gson
    private val service: ScheduleService

    init {
        gson = GsonBuilder().registerTypeAdapter(OffsetDateTime::class.java, JsonDeserializer { json, _, _ ->
            OffsetDateTime.parse(json.asString)
        }).create()

        service = ScheduleService.create(gson)
    }

    fun schedule(from: OffsetDateTime, locale: Locale, conference: Conference): Single<String> {
        return service.getSchedule(locale.language, conference.rawValue)
                .map { it.schedule }
                .flatMapIterable { it }
                .filter { !from.isAfter(it.datestamp) }
                .map {
                    val date = it.datestamp.format(ofPattern("EEEE, d MMMM y", locale)).capitalize()
                    val time = it.datestamp.format(ofPattern("HH:mm", locale))
                    Item(it.title, it.speaker.joinToString(), date, time)
                }
                .toList()
                .toJson(gson)
    }
}
