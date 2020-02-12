package com.ireny.warrantyreport.utils

import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.util.*


private val booleanAsIntAdapter = object : TypeAdapter<Boolean>() {

    override fun write(out: JsonWriter, value: Boolean?) {
        if(value != null) out.value(value) else out.nullValue()
    }

    override fun read(`in`: JsonReader): Boolean? {
        return when (val peek = `in`.peek()) {
            JsonToken.BOOLEAN -> `in`.nextBoolean()
            JsonToken.NULL -> {
                `in`.nextNull()
                null
            }
            JsonToken.NUMBER -> `in`.nextInt() != 0
            JsonToken.STRING -> `in`.nextString()?.toBoolean()
            else -> throw IllegalStateException("Expected BOOLEAN or NUMBER but was $peek")
        }
    }
}

private var serializer: JsonSerializer<Date> = JsonSerializer { src, _, _ ->
    if(src == null)  null else  JsonPrimitive(src.time)
}

private  var deserializer: JsonDeserializer<Date> = JsonDeserializer<Date> { json, _, _ ->
    if(json == null) null else Date(json.asLong)
}

var gson = GsonBuilder()
        .registerTypeAdapter(Boolean::class.java, booleanAsIntAdapter)
        .registerTypeAdapter(Boolean::class.javaPrimitiveType, booleanAsIntAdapter)
        .registerTypeAdapter(Date::class.java, serializer)
        .registerTypeAdapter(Date::class.java, deserializer)
        .create()
