package com.romainpiel.catsanddogs.api

import com.romainpiel.catsanddogs.api.model.ApiConference
import io.reactivex.Observable
import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder

// See: https://square.github.io/retrofit/ for details of how this works uisng Retrofit
interface ScheduleService {
    // TODO: @Header("Accept-Language") does not seem to be working
    @GET("/{conference}/schedule")
    fun getSchedule(@Header("Accept-Language") acceptLanguage: String,
                    @Path("conference") conference: String): Observable<ApiConference>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(gson: Gson): ScheduleService {
            val retrofit = Retrofit.Builder()
                    .baseUrl("https://catsanddogs-swift-server.herokuapp.com/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

            return retrofit.create(ScheduleService::class.java)
        }
    }
}
