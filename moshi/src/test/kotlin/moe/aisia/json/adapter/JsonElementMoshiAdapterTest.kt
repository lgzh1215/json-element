package moe.aisia.json.adapter

import com.squareup.moshi.Moshi
import io.kotlintest.matchers.*
import moe.aisia.json.JsonElement
import moe.aisia.json.JsonObject
import org.junit.Test

class JsonElementMoshiAdapterTest {
   @Test
   fun test() {

      val inputStream = JsonElement::class.java.classLoader.getResourceAsStream("api_start2_20140810.json")
      val json = inputStream.readBytes().toString(Charsets.UTF_8)
      json should startWith("""{"api_result":1,"api_result_msg":"\u6210\u529f","api_data":{"api_mst_ship":[{"api_id":1,""")

      val adapter = Moshi.Builder().add(JsonElement::class.java, JsonElementMoshiAdapter(true))
            .build().adapter(JsonElement::class.java)
      val jsonElement = adapter.fromJson(json)

      jsonElement shouldNotBe null
      jsonElement!! should beOfType<JsonObject>()

      val toJson = adapter.toJson(jsonElement)
      toJson should startWith("""{"api_result":1,"api_result_msg":"成功","api_data":{"api_mst_ship":[{"api_id":1,""")

      jsonElement["api_result"].asInt() shouldBe 1
      jsonElement["api_result_msg"].asString() shouldBe "成功"
      jsonElement["api_data"]["api_mst_ship"][152]["api_name"].asString() shouldBe "大鳳"
      jsonElement["api_data"]["api_mst_ship"][155]["api_yomi"].asString() shouldBe "たいほう"

      jsonElement["api_data"]["api_mst_slotitem"][55]["api_name"].asString() shouldBe "震電改"
      jsonElement["api_data"]["api_mst_slotitem"][55]["api_tyku"].asLong() shouldBe 15L
   }
}
