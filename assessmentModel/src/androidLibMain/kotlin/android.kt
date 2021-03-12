package org.sagebionetworks.assessmentmodel

import android.os.Build
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

actual class Platform actual constructor() {
    actual val platform: String = "Android"
}

actual class Product(actual val user: String) {
    fun androidSpecificOperation() {
        println("I am ${Build.MODEL} by ${Build.MANUFACTURER}")
    }

    override fun toString() = "Android product of $user for ${Build.MODEL}"
}

actual object Factory {
    actual fun create(config: Map<String, String>) =
        Product(config["user"]!!)

    actual val platform: String = "android"
}

actual object UUIDGenerator {
    actual fun uuidString() : String = UUID.randomUUID().toString()
}

actual object DateGenerator {
    actual fun nowString(): String = ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
    
    actual fun currentYear(): Int = ZonedDateTime.now().year
}
