package com.romainpiel.catsanddogs.api.model

import java.time.OffsetDateTime

data class ApiEvent(val title: String, val speaker: List<String>, val datestamp: OffsetDateTime)