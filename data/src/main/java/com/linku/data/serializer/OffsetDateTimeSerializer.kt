package com.linku.data.serializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import com.linku.data.util.toOffsetDateTimeInKorea
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * API의 로컬 날짜·시간 문자열을 한국 표준시가 적용된 [OffsetDateTime]으로 변환합니다.
 *
 * nullable DTO 필드에 명시적인 JSON `null`이 전달되면 문자열 파싱을 시도하지 않고 `null`을 반환합니다.
 */
class OffsetDateTimeSerializer: JsonDeserializer<OffsetDateTime>, JsonSerializer<OffsetDateTime>,
    JsonAdapter<OffsetDateTime>() {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): OffsetDateTime {
        return OffsetDateTime.parse(json.asString, formatter)
    }

    override fun serialize(
        src: OffsetDateTime,
        typeOfSrc: Type,
        context: JsonSerializationContext,
    ): JsonElement {
        return JsonPrimitive(src.format(formatter))
    }

    @FromJson
    override fun fromJson(jsonReader: JsonReader): OffsetDateTime? {
        // JSON null을 nextString()으로 읽으면 JsonDataException이 발생하므로 먼저 소비합니다.
        if (jsonReader.peek() == JsonReader.Token.NULL) {
            return jsonReader.nextNull()
        }

        val string = jsonReader.nextString()
        val localDateTime = LocalDateTime.parse(string, formatter)
        return localDateTime.toOffsetDateTimeInKorea()
    }

    @ToJson
    override fun toJson(jsonReader: JsonWriter, value: OffsetDateTime?) {
        jsonReader.value(value?.format(formatter))
    }
}
