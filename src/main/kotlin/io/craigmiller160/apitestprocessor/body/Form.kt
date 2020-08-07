package io.craigmiller160.apitestprocessor.body

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class Form : HashMap<String,String>(), Body {
    fun toUrlEncoded(): String {
        return this.entries
                .map { (key, value) ->
                    val encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8)
                    val encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8)
                    return "$encodedKey=$encodedValue"
                }
                .joinToString("&")
    }
}

fun formOf(vararg pairs: Pair<String,String>): Form = pairs.toMap(Form())
