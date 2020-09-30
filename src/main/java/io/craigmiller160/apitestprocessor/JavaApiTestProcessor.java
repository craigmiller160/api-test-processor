package io.craigmiller160.apitestprocessor;

import io.craigmiller160.apitestprocessor.config.SetupConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.util.function.Consumer;

public class JavaApiTestProcessor {

    public static JavaApiTestProcessor createProcessor(final Consumer<SetupConfig> setup) {
        final var processor = new ApiTestProcessor(setupConfig -> {
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
