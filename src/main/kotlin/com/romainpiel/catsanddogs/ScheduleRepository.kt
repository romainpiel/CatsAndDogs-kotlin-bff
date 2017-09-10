package com.romainpiel.catsanddogs

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.romainpiel.catsanddogs.api.ScheduleService
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import io.reactivex.rxkotlin.toSingle

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

fun Single<List<Item>>.toHtml(): Single<String> {
    return this.map { it.map { it }.fold("", { acc, item ->
        val title = item.title
        val subtitle = item.subtitle
        val time = item.time
        val date = item.date

        acc + "<hr><p>" +
                "<b>$title</b><br>" +
                "$subtitle<br>" +
                "$time, $date" +
                "</p>"
    }) }
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

    fun scheduleHtml(from: OffsetDateTime, locale: Locale, conference: Conference): Single<String> {
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
                .toHtml()
    }
}
