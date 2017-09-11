package com.romainpiel.catsanddogs.model

enum class Conference(val rawValue: String) {
    MCE4("mce4"),
    KotlinConf("kotlinconf");

    companion object {
        fun instance(rawValue: String?) = Conference.values().firstOrNull { it.rawValue == rawValue }
    }
}
