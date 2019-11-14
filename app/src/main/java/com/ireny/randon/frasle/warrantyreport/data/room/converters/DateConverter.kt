package com.ireny.randon.frasle.warrantyreport.data.room.converters

import androidx.room.TypeConverter
import java.util.*


class DateConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}