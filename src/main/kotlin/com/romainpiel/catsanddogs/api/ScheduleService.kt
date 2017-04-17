package com.romainpiel.catsanddogs.api

import com.romainpiel.catsanddogs.api.model.ApiConference
import io.reactivex.Observable
import retrofit2.http.GET

interface ScheduleService {
    @GET("/schedule.json")
    fun getConference(): Observable<ApiConference>
}