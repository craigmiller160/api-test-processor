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

import io.craigmiller160.apitestprocessor.body.Body
import org.springframework.http.HttpMethod
import java.util.function.Consumer

class RequestConfig {
    var method: HttpMethod = HttpMethod.GET
    var path: String = ""
    var body: Body? = null
    var headers: Map<String,String> = mapOf()
    internal var overrideAuth: AuthConfig? = null

    fun overrideAuth(init: Consumer<AuthConfig>) {
        overrideAuth { init.accept(this) }
    }

    fun overrideAuth(init: AuthConfig.() -> Unit) {
        overrideAuth = AuthConfig()
        overrideAuth?.init()
    }
}
