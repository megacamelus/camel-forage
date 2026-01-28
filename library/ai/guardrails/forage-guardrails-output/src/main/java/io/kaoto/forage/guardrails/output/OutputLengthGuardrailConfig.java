package io.kaoto.forage.guardrails.output;

import static io.kaoto.forage.guardrails.output.OutputLengthGuardrailConfigEntries.*;

import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import java.util.Optional;

/**
 * Configuration class for output length guardrail.
 */
public class OutputLengthGuardrailConfig implements Config {

    private final String prefix;

    public OutputLengthGuardrailConfig() {
        this(null);
    }

    public OutputLengthGuardrailConfig(String prefix) {
        this.prefix = prefix;

        OutputLengthGuardrailConfigEntries.register(prefix);
        ConfigStore.getInstance().load(OutputLengthGuardrailConfig.class, this, this::register);
        OutputLengthGuardrailConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = OutputLengthGuardrailConfigEntries.find(prefix, name);
        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    @Override
    public String name() {
        return "forage-guardrail-output-length";
    }

    public int maxChars() {
        return ConfigStore.getInstance()
                .get(MAX_CHARS.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(50000);
    }

    public int minChars() {
        return ConfigStore.getInstance()
                .get(MIN_CHARS.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(1);
    }

    public boolean truncateOnOverflow() {
        return ConfigStore.getInstance()
                .get(TRUNCATE_ON_OVERFLOW.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }
}
