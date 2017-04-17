package com.romainpiel.catsanddogs

import com.romainpiel.catsanddogs.api.ScheduleService
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ScheduleRepository {
    private val service: ScheduleService

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://catsanddogs-kotlin-server.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        service = retrofit.create(ScheduleService::class.java)
    }

    fun schedule(): Single<List<Card>> {
        return service.getConference()
                .map { it.schedule }
                .flatMapIterable { it }
                .map { Card(it.title, it.speaker.joinToString()) }
                .toList()
    }
}