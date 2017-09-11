package com.romainpiel.catsanddogs.repository

import com.google.gson.Gson
import com.romainpiel.catsanddogs.model.Item
import io.reactivex.Single

fun <T> Single<T>.toJson(gson: Gson) : Single<String> {
    return this.map { gson.toJson(it) }
}

fun Single<List<Item>>.toHtml(): Single<String> {
    return this.map  { it.fold("", { acc, item ->
        acc + "<hr><p>" +
                "<b>${item.title}</b><br>" +
                "${item.subtitle}<br>" +
                "${item.time}, ${item.date}" +
                "</p>"
    }) }
}

