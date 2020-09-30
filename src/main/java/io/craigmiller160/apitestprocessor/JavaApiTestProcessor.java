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

package io.craigmiller160.apitestprocessor;

import io.craigmiller160.apitestprocessor.config.SetupConfig;
import java.util.function.Consumer;

public class JavaApiTestProcessor {

    public static JavaApiTestProcessor createProcessor(final Consumer<SetupConfig> setup) {
        final var processor = new ApiTestProcessor(setupConfig -> {
            setupConfig.auth(config -> {
                return null;
            });

            if (setup != null) {
                setup.accept(setupConfig);
            }
            return null;
        });
        return new JavaApiTestProcessor(processor);
    }

    private final ApiTestProcessor apiTestProcessor;

    private JavaApiTestProcessor(final ApiTestProcessor apiTestProcessor) {
        this.apiTestProcessor = apiTestProcessor;
    }
}
