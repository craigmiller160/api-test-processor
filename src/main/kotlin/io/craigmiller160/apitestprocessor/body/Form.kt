package io.craigmiller160.apitestprocessor.body

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class Form : HashMap<String,String>(), Body {
    fun toUrlEncoded(): String {
        return this.entries
                .joinToString("&") { entry ->
                    val encodedKey = URLEncoder.encode(entry.key, StandardCharsets.UTF_8)
                    val encodedValue = URLEncoder.encode(entry.value, StandardCharsets.UTF_8)
                    "$encodedKey=$encodedValue"
                }
    }
}

fun formOf(vararg pairs: Pair<String,String>): Form = pairs.toMap(Form())
