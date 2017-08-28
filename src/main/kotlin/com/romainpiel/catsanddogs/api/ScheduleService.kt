package com.romainpiel.catsanddogs.api

import com.romainpiel.catsanddogs.api.model.ApiConference
import io.reactivex.Observable
import retrofit2.http.GET

interface ScheduleService {
    @GET("/mce4/schedule")
    fun getMCE4Schedule(): Observable<ApiConference>

    @GET("/kotlinconf/schedule")
    fun getKotlinConfSchedule(): Observable<ApiConference>
}
