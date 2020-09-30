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

package io.craigmiller160.apitestprocessor.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.test.web.servlet.MockMvc
import java.util.function.Consumer

class SetupConfig {
    lateinit var mockMvc: MockMvc
    lateinit var objectMapper: ObjectMapper
    var isSecure = false

    internal val authConfig = AuthConfig()

    fun auth(init: AuthConfig.() -> Unit) {
        authConfig.init()
    }

    fun auth(init: Consumer<AuthConfig>) {
        auth { init.accept(this) }
    }

}
