/*
 *     Financial Manager API
 *     Copyright (C) 2020 Craig Miller
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.craigmiller160.apitestprocessor.body

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class Form : HashMap<String,String>(), Body {
    fun toUrlEncoded(): String {
        return this.entries
                .joinToString("&") { (key, value) ->
                    val encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8)
                    val encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8)
                    "$encodedKey=$encodedValue"
                }
    }
}

fun formOf(vararg pairs: Pair<String,String>): Form = pairs.toMap(Form())
