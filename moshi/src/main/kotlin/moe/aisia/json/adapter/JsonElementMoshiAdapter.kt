package moe.aisia.json.adapter

import com.squareup.moshi.*
import com.squareup.moshi.JsonReader.Token.*
import moe.aisia.json.*

class JsonElementMoshiAdapter(private val ordered: Boolean = false) : JsonAdapter<JsonElement>() {
   override fun fromJson(reader: JsonReader): JsonElement = with(reader) {
      when (reader.peek()) {
         BEGIN_ARRAY -> {
            JsonArray().apply {
               beginArray()
               while (hasNext()) {
                  add(fromJson(reader))
               }
               endArray()
            }
         }
         BEGIN_OBJECT -> {
            JsonObject(ordered).apply {
               beginObject()
               while (hasNext()) {
                  val name = nextName()
                  val value = fromJson(reader)
                  val replaced = put(name, value)
                  if (replaced != null)
                     throw JsonDataException("Map key '$name' has multiple values at path $path: $replaced and $value")
               }
               endObject()
            }
         }
         STRING -> JsonPrimitive.Str(nextString())
         NUMBER -> {
            JsonPrimitive.Num(nextString().run {
               (toIntOrNull() ?: toLongOrNull() ?: toDoubleOrNull() ?: toBigDecimal()) as Number
            })
         }
         BOOLEAN -> JsonPrimitive.Bool(nextBoolean())
         NULL -> {
            nextNull<Nothing>()
            JsonNull
         }
         else -> throw IllegalStateException("Expected a value but was ${peek()} at path $path")
      }
   }

   override fun toJson(writer: JsonWriter, value: JsonElement?) {
      if (value == null) {
         writer.nullValue()
      } else {
         toJson(writer, value)
      }
   }

   @Suppress("UNUSED_PARAMETER")
   private fun toJson(writer: JsonWriter, value: JsonElement, fuck: Unit = Unit): Unit = with(writer) {
      when (value) {
         is JsonObject -> {
            beginObject()
            for ((key, jsonElement) in value) {
               name(key)
               toJson(writer, jsonElement)
            }
            endObject()
         }
         is JsonArray -> {
            beginArray()
            for (jsonElement in value) {
               toJson(writer, jsonElement)
            }
            endArray()
         }
         is JsonPrimitive.Num -> value(value.asNumber())
         is JsonPrimitive.Bool -> value(value.asBoolean())
         is JsonPrimitive.Str -> value(value.asString())
         is JsonNull -> nullValue()
      }
   }
}
