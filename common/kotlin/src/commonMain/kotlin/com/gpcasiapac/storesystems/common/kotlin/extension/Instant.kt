package com.gpcasiapac.storesystems.common.kotlin.extension

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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

// New flexible local formatting API without abbreviated val or function names

/**
 * Select which parts to include in the output string.
 * - DateOnly: only the calendar date.
 * - TimeOnly: only the clock time.
 * - DateTime: both date and time joined with ", ".
 *
 * Examples (using 5 November 2025, 15:07:42 local time):
 * - DateOnly + DateStyle.Medium => "5 Nov 2025"
 * - TimeOnly + TimeStyle.Short + HourCycle.H12 => "3:07 PM"
 * - DateTime + DateStyle.Long + TimeStyle.Long + HourCycle.H24 => "5 November 2025, 15:07:42"
 */
enum class Components { DateOnly, TimeOnly, DateTime }

/**
 * Controls how verbose the date portion is.
 * - Short: day and abbreviated month, no year (e.g., "5 Nov").
 * - Medium: day, abbreviated month, year (e.g., "5 Nov 2025").
 * - Long: day, full month name, year (e.g., "5 November 2025").
 */
enum class DateStyle { Short, Medium, Long }

/**
 * Controls how verbose the time portion is.
 * - Short: hours and minutes (e.g., H12 "3:07 PM", H24 "15:07").
 * - Medium: same as Short for simplicity.
 * - Long: includes seconds (e.g., H12 "3:07:42 PM", H24 "15:07:42").
 */
enum class TimeStyle { Short, Medium, Long }

/**
 * Selects the clock format for time output.
 * - H12: 12-hour clock with AM/PM (e.g., "3:07 PM").
 * - H24: 24-hour clock (e.g., "15:07").
 */
enum class HourCycle { H12, H24 }

private fun Instant.toLocalDateTimeInTimeZone(timeZone: TimeZone): LocalDateTime {
    val epochMilliseconds = this.toEpochMilliseconds()
    val dateTimeInstant = kotlinx.datetime.Instant.fromEpochMilliseconds(epochMilliseconds)
    return dateTimeInstant.toLocalDateTime(timeZone)
}

private fun Month.monthAbbreviation(): String = when (this) {
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

private fun Month.fullName(): String = when (this) {
    Month.JANUARY -> "January"
    Month.FEBRUARY -> "February"
    Month.MARCH -> "March"
    Month.APRIL -> "April"
    Month.MAY -> "May"
    Month.JUNE -> "June"
    Month.JULY -> "July"
    Month.AUGUST -> "August"
    Month.SEPTEMBER -> "September"
    Month.OCTOBER -> "October"
    Month.NOVEMBER -> "November"
    Month.DECEMBER -> "December"
}

private fun padToTwoDigits(value: Int): String = if (value < 10) "0$value" else value.toString()

/**
 * Format this Instant into a local date/time string using simple, memorable presets.
 *
 * Examples assume a local date-time of 5 November 2025 at 15:07:42 in the selected time zone.
 *
 * Examples:
 * - components = DateOnly, dateStyle = Short                      => "5 Nov"
 * - components = DateOnly, dateStyle = Medium                     => "5 Nov 2025"
 * - components = DateOnly, dateStyle = Long                       => "5 November 2025"
 *
 * - components = TimeOnly, timeStyle = Short, hourCycle = H12     => "3:07 PM"
 * - components = TimeOnly, timeStyle = Short, hourCycle = H24     => "15:07"
 * - components = TimeOnly, timeStyle = Long,  hourCycle = H12     => "3:07:42 PM"
 * - components = TimeOnly, timeStyle = Long,  hourCycle = H24     => "15:07:42"
 *
 * - components = DateTime, dateStyle = Medium, timeStyle = Short, hourCycle = H12 => "5 Nov 2025, 3:07 PM"
 * - components = DateTime, dateStyle = Long,   timeStyle = Long,  hourCycle = H24 => "5 November 2025, 15:07:42"
 */
fun Instant.formatLocal(
    components: Components = Components.DateTime,
    dateStyle: DateStyle = DateStyle.Medium,
    timeStyle: TimeStyle = TimeStyle.Short,
    hourCycle: HourCycle = HourCycle.H12,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val localDateTime = this.toLocalDateTimeInTimeZone(timeZone)

    fun datePart(): String = when (dateStyle) {
        DateStyle.Short -> "${localDateTime.day} ${localDateTime.month.monthAbbreviation()}"
        DateStyle.Medium -> "${localDateTime.day} ${localDateTime.month.monthAbbreviation()} ${localDateTime.year}"
        DateStyle.Long -> "${localDateTime.day} ${localDateTime.month.fullName()} ${localDateTime.year}"
    }

    fun timePart(): String {
        val hour = localDateTime.hour
        val minute = padToTwoDigits(localDateTime.minute)
        val second = padToTwoDigits(localDateTime.second)
        return when (hourCycle) {
            HourCycle.H12 -> {
                val isAnteMeridiem = hour < 12
                val hourInTwelveHourClock = when {
                    hour == 0 -> 12
                    hour > 12 -> hour - 12
                    else -> hour
                }
                val anteMeridiemOrPostMeridiem = if (isAnteMeridiem) "AM" else "PM"
                when (timeStyle) {
                    TimeStyle.Short, TimeStyle.Medium -> "$hourInTwelveHourClock:$minute $anteMeridiemOrPostMeridiem"
                    TimeStyle.Long -> "$hourInTwelveHourClock:$minute:$second $anteMeridiemOrPostMeridiem"
                }
            }
            HourCycle.H24 -> {
                val hourTwoDigits = padToTwoDigits(hour)
                when (timeStyle) {
                    TimeStyle.Short, TimeStyle.Medium -> "$hourTwoDigits:$minute"
                    TimeStyle.Long -> "$hourTwoDigits:$minute:$second"
                }
            }
        }
    }

    return when (components) {
        Components.DateOnly -> datePart()
        Components.TimeOnly -> timePart()
        Components.DateTime -> "${datePart()}, ${timePart()}"
    }
}

// Shortcut functions
/**
 * Date-only, very short label.
 * Example (5 Nov 2025): "5 Nov"
 */
fun Instant.toLocalDateShortString(timeZone: TimeZone = TimeZone.currentSystemDefault()): String =
    formatLocal(components = Components.DateOnly, dateStyle = DateStyle.Short, timeZone = timeZone)

/**
 * Date-only, medium detail.
 * Example (5 Nov 2025): "5 Nov 2025"
 */
fun Instant.toLocalDateMediumString(timeZone: TimeZone = TimeZone.currentSystemDefault()): String =
    formatLocal(components = Components.DateOnly, dateStyle = DateStyle.Medium, timeZone = timeZone)

/**
 * Date-only, long detail.
 * Example (5 Nov 2025): "5 November 2025"
 */
fun Instant.toLocalDateLongString(timeZone: TimeZone = TimeZone.currentSystemDefault()): String =
    formatLocal(components = Components.DateOnly, dateStyle = DateStyle.Long, timeZone = timeZone)

/**
 * Time-only, short (hours:minutes).
 * Examples (15:07:42):
 * - HourCycle.H12 => "3:07 PM"
 * - HourCycle.H24 => "15:07"
 */
fun Instant.toLocalTimeShortString(
    hourCycle: HourCycle = HourCycle.H12,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String = formatLocal(
    components = Components.TimeOnly,
    timeStyle = TimeStyle.Short,
    hourCycle = hourCycle,
    timeZone = timeZone
)

/**
 * Time-only, medium (same as short for simplicity).
 * Examples (15:07:42):
 * - HourCycle.H12 => "3:07 PM"
 * - HourCycle.H24 => "15:07"
 */
fun Instant.toLocalTimeMediumString(
    hourCycle: HourCycle = HourCycle.H12,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String = formatLocal(
    components = Components.TimeOnly,
    timeStyle = TimeStyle.Medium,
    hourCycle = hourCycle,
    timeZone = timeZone
)

/**
 * Time-only, long (includes seconds).
 * Examples (15:07:42):
 * - HourCycle.H12 => "3:07:42 PM"
 * - HourCycle.H24 => "15:07:42"
 */
fun Instant.toLocalTimeLongString(
    hourCycle: HourCycle = HourCycle.H12,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String = formatLocal(
    components = Components.TimeOnly,
    timeStyle = TimeStyle.Long,
    hourCycle = hourCycle,
    timeZone = timeZone
)

/**
 * Date and time, short presets.
 * Examples (5 Nov 2025 15:07:42):
 * - HourCycle.H12 => "5 Nov, 3:07 PM"
 * - HourCycle.H24 => "5 Nov, 15:07"
 */
fun Instant.toLocalDateTimeShortString(
    hourCycle: HourCycle = HourCycle.H12,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String = formatLocal(
    components = Components.DateTime,
    dateStyle = DateStyle.Short,
    timeStyle = TimeStyle.Short,
    hourCycle = hourCycle,
    timeZone = timeZone
)

/**
 * Date and time, medium presets (default), equivalent to toLocalDateTimeString().
 * Examples (5 Nov 2025 15:07:42):
 * - HourCycle.H12 => "5 Nov 2025, 3:07 PM"
 * - HourCycle.H24 => "5 Nov 2025, 15:07"
 */
fun Instant.toLocalDateTimeMediumString(
    hourCycle: HourCycle = HourCycle.H12,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String = formatLocal(
    components = Components.DateTime,
    dateStyle = DateStyle.Medium,
    timeStyle = TimeStyle.Short,
    hourCycle = hourCycle,
    timeZone = timeZone
)

/**
 * Date and time, long presets (full month name and includes seconds).
 * Examples (5 Nov 2025 15:07:42):
 * - HourCycle.H12 => "5 November 2025, 3:07:42 PM"
 * - HourCycle.H24 => "5 November 2025, 15:07:42"
 */
fun Instant.toLocalDateTimeLongString(
    hourCycle: HourCycle = HourCycle.H12,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String = formatLocal(
    components = Components.DateTime,
    dateStyle = DateStyle.Long,
    timeStyle = TimeStyle.Long,
    hourCycle = hourCycle,
    timeZone = timeZone
)

// International standard helpers for interop/logs
/**
 * ISO 8601-like local date string (yyyy-MM-dd).
 * Example (5 Nov 2025): "2025-11-05"
 */
fun Instant.toLocalDateStringInInternationalStandardFormat(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val localDateTime = this.toLocalDateTimeInTimeZone(timeZone)
    return "${localDateTime.year}-${padToTwoDigits(localDateTime.monthNumber)}-${padToTwoDigits(localDateTime.day)}"
}

/**
 * ISO 8601-like local date-time string (yyyy-MM-dd'T'HH:mm:ss).
 * Example (5 Nov 2025 15:07:42): "2025-11-05T15:07:42"
 */
fun Instant.toLocalDateTimeStringInInternationalStandardFormat(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    val localDateTime = this.toLocalDateTimeInTimeZone(timeZone)
    val date = "${localDateTime.year}-${padToTwoDigits(localDateTime.monthNumber)}-${padToTwoDigits(localDateTime.day)}"
    val time = "${padToTwoDigits(localDateTime.hour)}:${padToTwoDigits(localDateTime.minute)}:${padToTwoDigits(localDateTime.second)}"
    return date + "T" + time
}

/**
 * Backward-compatible alias for the medium date-time preset.
 * Equivalent to: toLocalDateTimeMediumString(HourCycle.H12, TimeZone.currentSystemDefault())
 * Example (5 Nov 2025 15:07:42): "5 Nov 2025, 3:07 PM"
 */
fun Instant.toLocalDateTimeString(): String = toLocalDateTimeMediumString()
