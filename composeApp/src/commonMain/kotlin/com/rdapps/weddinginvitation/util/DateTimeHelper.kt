package com.rdapps.weddinginvitation.util

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun LocalDateTime.Companion.now(): LocalDateTime = Clock.System.now()
    .toLocalDateTime(TimeZone.currentSystemDefault())

fun LocalDateTime.toMillis(): Long = this.toInstant(TimeZone.currentSystemDefault())
    .toEpochMilliseconds()