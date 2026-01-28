package io.kaoto.forage.guardrails.output;

import static io.kaoto.forage.guardrails.output.JsonFormatGuardrailConfigEntries.*;

import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import java.util.Arrays;
import java.util.Optional;

/**
 * Configuration class for JSON format guardrail.
 */
public class JsonFormatGuardrailConfig implements Config {

    private final String prefix;

    public JsonFormatGuardrailConfig() {
        this(null);
    }

    public JsonFormatGuardrailConfig(String prefix) {
        this.prefix = prefix;

        JsonFormatGuardrailConfigEntries.register(prefix);
        ConfigStore.getInstance().load(JsonFormatGuardrailConfig.class, this, this::register);
        JsonFormatGuardrailConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = JsonFormatGuardrailConfigEntries.find(prefix, name);
        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    @Override
    public String name() {
        return "forage-guardrail-json-format";
    }

    public String[] requiredFields() {
        return ConfigStore.getInstance()
                .get(REQUIRED_FIELDS.asNamed(prefix))
                .map(s -> Arrays.stream(s.split(","))
                        .map(String::trim)
                        .filter(f -> !f.isEmpty())
                        .toArray(String[]::new))
                .orElse(new String[0]);
    }

    public boolean extractJson() {
        return ConfigStore.getInstance()
                .get(EXTRACT_JSON.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(true);
    }

    public boolean allowArray() {
        return ConfigStore.getInstance()
                .get(ALLOW_ARRAY.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(true);
    }
}
