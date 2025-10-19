package com.gpcasiapac.storesystems.common.kotlin.extension

import kotlinx.datetime.LocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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

/**
 * Formats this Instant into a human-readable local date/time string.
 * Example: "19 Oct 2025, 12:10 AM" (uses the current system time zone).
 * Multiplatform-safe (uses kotlinx.datetime under the hood).
 */
fun Instant.toLocalDateTimeString(): String {
    // Convert kotlin.time.Instant -> kotlinx.datetime.Instant
    val epochMillis = this.toEpochMilliseconds()
    val kxInstant = Instant.fromEpochMilliseconds(epochMillis)
    val tz = TimeZone.currentSystemDefault()
    val ldt = kxInstant.toLocalDateTime(tz)

    val day = ldt.day
    val month = ldt.month
    val monthAbbr = when (month) {
        Month.JANUARY -> "Jan"
        Month.FEBRUARY -> "Feb"
        Month.MARCH -> "Mar"
        Month.APRIL -> "Apr"
        Month.MAY -> "May"
        Month.JUNE -> "Jun"
        Month.JULY -> "Jul"
        Month.AUGUST -> "Aug"
        Month.SEPTEMBER -> "Sep"
        Month.OCTOBER -> "Oct"
        Month.NOVEMBER -> "Nov"
        Month.DECEMBER -> "Dec"
    }

    val year = ldt.year

    val hour = ldt.hour
    val minute = ldt.minute
    val isAm = hour < 12
    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    val minutePadded = if (minute < 10) "0$minute" else "$minute"
    val amPm = if (isAm) "AM" else "PM"

    return "$day $monthAbbr $year, $hour12:$minutePadded $amPm"
}
