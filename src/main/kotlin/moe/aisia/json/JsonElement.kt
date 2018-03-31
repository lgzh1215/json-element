@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "ReplaceCallWithBinaryOperator", "unused")

package moe.aisia.json

import java.math.BigDecimal

sealed class JsonElement {
   open operator fun get(index: Int): JsonElement = JsonNull
   open operator fun get(index: Integer): JsonElement = JsonNull
   open operator fun get(key: String): JsonElement = JsonNull

   open fun asString(default: String = ""): String = default
   open fun asBoolean(default: Boolean = false): Boolean = default
   open fun asNumber(default: Number = 0): Number = default
   open fun asInt(default: Int = 0): Int = asNumber(default).toInt()
   open fun asLong(default: Long = 0L): Long = asNumber(default).toLong()
   open fun asDouble(default: Double = 0.0): Double = asNumber(default).toDouble()
   open fun asBigDecimal(default: BigDecimal = BigDecimal.ZERO): BigDecimal = asString().toBigDecimalOrNull() ?: default
}

class JsonArray(private val list: MutableList<JsonElement>) : JsonElement(),
      MutableList<JsonElement> by list, RandomAccess, Cloneable {
   constructor() : this(ArrayList())
   constructor(initialCapacity: Int) : this(ArrayList(initialCapacity))

   override fun get(index: Int): JsonElement = list[index]
   override fun get(index: Integer): JsonElement = this[index.toInt()]
   override fun get(key: String): JsonElement = list[key.toInt()]

   fun toArray(): Array<Any> {
      list as java.util.List<*>?
      return list.toArray()
   }

   fun <T> toArray(a: Array<T>): Array<T> {
      list as java.util.List<*>?
      return list.toArray(a)
   }

   override fun clone(): JsonArray = JsonArray(ArrayList(list))

   override fun toString(): String {
      return list.toString()
   }

   override fun equals(other: Any?): Boolean {
      return if (this === other) true
      else if (other == null || other !is JsonArray?) false
      else list.equals(other.list)
   }

   override fun hashCode(): Int {
      return list.hashCode()
   }
}

class JsonObject(private val map: MutableMap<String, JsonElement>) : JsonElement(),
      MutableMap<String, JsonElement> by map, Cloneable {
   constructor() : this(16, false)
   constructor(ordered: Boolean) : this(16, ordered)
   constructor(initialCapacity: Int) : this(initialCapacity, false)
   constructor(initialCapacity: Int, ordered: Boolean) : this(
         if (ordered) LinkedHashMap(initialCapacity)
         else HashMap<String, JsonElement>(initialCapacity)
   )

   override fun get(index: Int): JsonElement = this[index.toString()]
   override fun get(index: Integer): JsonElement = this[index.toString()]
   override fun get(key: String): JsonElement = map[key] ?: JsonNull

   override fun toString(): String {
      return map.toString()
   }

   override fun equals(other: Any?): Boolean {
      return if (this === other) true
      else if (other == null || other !is JsonObject?) false
      else map.equals(other.map)
   }

   override fun hashCode(): Int {
      return map.hashCode()
   }
}

sealed class JsonPrimitive : JsonElement() {
   class Num(private val value: Number) : JsonPrimitive() {
      override fun asString(default: String): String = value.toString()
      override fun asNumber(default: Number): Number = value
   }

   class Str(private val value: String) : JsonPrimitive() {
      override fun asString(default: String): String = value
      override fun asNumber(default: Number): Number = value.run {
         (toIntOrNull() ?: toLongOrNull() ?: toDoubleOrNull() ?: toBigDecimal()) as Number
      }
   }

   class Bool(private val value: Boolean) : JsonPrimitive() {
      override fun asString(default: String): String = value.toString()
      override fun asBoolean(default: Boolean): Boolean = value
   }

   override fun toString(): String = asString()
}

object JsonNull : JsonElement() {
   override fun toString(): String = "null"
}

// TODO deepClone, Serializable