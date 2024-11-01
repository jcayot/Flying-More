package com.cayot.flyingmore.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

fun updateInstantDate(currentInstant: Instant?, dateInstant: Instant) : Instant {
    if (currentInstant == null)
        return (dateInstant)
    val currentTime: LocalTime = currentInstant.atZone(ZoneId.systemDefault()).toLocalTime()

    val newDate = dateInstant.atZone(ZoneId.systemDefault()).toLocalDate()

    val updatedLocalDateTime = LocalDateTime.of(newDate.year, newDate.monthValue, newDate.dayOfMonth,
        currentTime.hour, currentTime.minute, currentTime.second, currentTime.nano)

    return (updatedLocalDateTime.atZone(ZoneId.systemDefault()).toInstant())
}

fun updateInstantHourMinute(currentInstant: Instant?, hour: Int, minute: Int) : Instant {
    val currentDate =
        if (currentInstant != null)
            currentInstant.atZone(ZoneId.systemDefault()).toLocalDate()
        else
            Instant.now().atZone(ZoneId.systemDefault()).toLocalDate()

    val newLocalTime = LocalTime.of(hour, minute)

    val updatedLocalDateTime = LocalDateTime.of(currentDate.year, currentDate.month, currentDate.dayOfMonth,
        newLocalTime.hour, newLocalTime.minute, newLocalTime.second, newLocalTime.nano)

    return (updatedLocalDateTime.atZone(ZoneId.systemDefault()).toInstant())
}
