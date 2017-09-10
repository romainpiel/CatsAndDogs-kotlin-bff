package com.romainpiel.catsanddogs.repository

import org.jetbrains.ktor.util.ValuesMap
import java.time.OffsetDateTime

fun ValuesMap.getDate(key: String): OffsetDateTime {
    val dateStr = get(key)
    val date = dateStr?.let { OffsetDateTime.parse(it) }

    return date ?: OffsetDateTime.MIN
}
