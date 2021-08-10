package br.com.zupacademy.rayllanderson.pix.utils

import com.google.protobuf.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class LocalDateTimeConverter {

    companion object {
        fun fromProtobufTimeStamp(date: Timestamp): LocalDateTime {
            return LocalDateTime.ofInstant(Instant.ofEpochSecond(date.seconds, date.nanos.toLong()), ZoneOffset.UTC)
        }
    }
}