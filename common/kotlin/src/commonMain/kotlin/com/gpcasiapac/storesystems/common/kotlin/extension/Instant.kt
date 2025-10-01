package com.gpcasiapac.storesystems.common.kotlin.extension

import kotlin.time.Clock
import kotlin.time.Instant

enum class TimeUnit {
    SECOND,
    MINUTE,
    HOUR,
    DAY,
    WEEK,
    MONTH,
    YEAR
}

fun Instant.toTimeAgoString(
    minLevel: TimeUnit = TimeUnit.MINUTE,
    maxLevel: TimeUnit = TimeUnit.DAY
): String {
    val now = Clock.System.now()
    val duration = now - this

    val seconds = duration.inWholeSeconds
    val minutes = duration.inWholeMinutes
    val hours = duration.inWholeHours
    val days = duration.inWholeDays

    return when {
        // Years
        days >= 365 && maxLevel >= TimeUnit.YEAR -> {
            val count = days / 365
            if (count == 1L) "1 year" else "$count years"
        }
        // Months
        days >= 30 && maxLevel >= TimeUnit.MONTH -> {
            val count = days / 30
            if (count == 1L) "1 month" else "$count months"
        }
        // Weeks
        days >= 7 && maxLevel >= TimeUnit.WEEK -> {
            val count = days / 7
            if (count == 1L) "1 week" else "$count weeks"
        }
        // Days
        hours >= 24 && maxLevel >= TimeUnit.DAY -> {
            val count = days
            if (count == 1L) "1 day" else "$count days"
        }
        // Hours
        minutes >= 60 && maxLevel >= TimeUnit.HOUR && minLevel <= TimeUnit.HOUR -> {
            val count = hours
            if (count == 1L) "1 hour" else "$count hours"
        }
        // Minutes
        seconds >= 60 && maxLevel >= TimeUnit.MINUTE && minLevel <= TimeUnit.MINUTE -> {
            val count = minutes
            if (count == 1L) "1 minute" else "$count minutes"
        }
        // Seconds (or if we're below minimum, show the minimum)
        minLevel <= TimeUnit.SECOND -> {
            val count = seconds
            if (count == 1L) "1 second" else "$count seconds"
        }
        // Below minimum level - show minimum level with value 1
        minLevel == TimeUnit.MINUTE -> "1 minute"
        minLevel == TimeUnit.HOUR -> "1 hour"
        minLevel == TimeUnit.DAY -> "1 day"
        minLevel == TimeUnit.WEEK -> "1 week"
        minLevel == TimeUnit.MONTH -> "1 month"
        else -> "1 year"
    }
}
