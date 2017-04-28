package com.romainpiel.catsanddogs

fun <T,U> T?.map(mapping: (T) -> U) = when (this) {
    null -> null
    else -> mapping(this)
}