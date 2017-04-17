package com.romainpiel.catsanddogs

import com.google.gson.Gson
import io.reactivex.Single

fun <T> Single<T>.toJson(gson: Gson) : Single<String> {
    return this.map { gson.toJson(it) }
}