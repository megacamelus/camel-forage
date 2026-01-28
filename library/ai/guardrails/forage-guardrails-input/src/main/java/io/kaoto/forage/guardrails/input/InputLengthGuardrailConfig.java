package io.kaoto.forage.guardrails.input;

import static io.kaoto.forage.guardrails.input.InputLengthGuardrailConfigEntries.*;

import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import java.util.Optional;

/**
 * Configuration class for input length guardrail.
 */
public class InputLengthGuardrailConfig implements Config {

    private final String prefix;

    public InputLengthGuardrailConfig() {
        this(null);
    }

    public InputLengthGuardrailConfig(String prefix) {
        this.prefix = prefix;

        InputLengthGuardrailConfigEntries.register(prefix);
        ConfigStore.getInstance().load(InputLengthGuardrailConfig.class, this, this::register);
        InputLengthGuardrailConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = InputLengthGuardrailConfigEntries.find(prefix, name);
        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    @Override
    public String name() {
        return "forage-guardrail-input-length";
    }

    public int maxChars() {
        return ConfigStore.getInstance()
                .get(MAX_CHARS.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(10000);
    }

    public int minChars() {
        return ConfigStore.getInstance()
                .get(MIN_CHARS.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(1);
    }
}
