package com.romainpiel.catsanddogs

import org.jetbrains.ktor.util.ValuesMap
import java.time.OffsetDateTime


fun ValuesMap.getDate(key: String): OffsetDateTime {
    val dateStr = get(key)
    val date = dateStr?.let { OffsetDateTime.parse(it) }
    val safeFrom = date ?: OffsetDateTime.MIN

    return safeFrom
}
