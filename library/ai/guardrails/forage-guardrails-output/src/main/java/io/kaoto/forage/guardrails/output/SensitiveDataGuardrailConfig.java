package io.kaoto.forage.guardrails.output;

import static io.kaoto.forage.guardrails.output.SensitiveDataGuardrailConfigEntries.*;

import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import org.apache.camel.component.langchain4j.agent.api.guardrails.SensitiveDataOutputGuardrail.Action;
import org.apache.camel.component.langchain4j.agent.api.guardrails.SensitiveDataOutputGuardrail.SensitiveDataType;

/**
 * Configuration class for sensitive data output guardrail.
 */
public class SensitiveDataGuardrailConfig implements Config {

    private final String prefix;

    public SensitiveDataGuardrailConfig() {
        this(null);
    }

    public SensitiveDataGuardrailConfig(String prefix) {
        this.prefix = prefix;

        SensitiveDataGuardrailConfigEntries.register(prefix);
        ConfigStore.getInstance().load(SensitiveDataGuardrailConfig.class, this, this::register);
        SensitiveDataGuardrailConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = SensitiveDataGuardrailConfigEntries.find(prefix, name);
        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    @Override
    public String name() {
        return "forage-guardrail-sensitive-data";
    }

    public Set<SensitiveDataType> detectTypes() {
        String typesStr = ConfigStore.getInstance()
                .get(DETECT_TYPES.asNamed(prefix))
                .orElse("API_KEY,AWS_KEY,SECRET,PRIVATE_KEY,CREDIT_CARD,SSN,JWT,CONNECTION_STRING,GITHUB_TOKEN");

        Set<SensitiveDataType> types = EnumSet.noneOf(SensitiveDataType.class);
        Arrays.stream(typesStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(s -> {
                    try {
                        types.add(SensitiveDataType.valueOf(s.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        // Ignore invalid types
                    }
                });

        return types.isEmpty() ? EnumSet.allOf(SensitiveDataType.class) : types;
    }

    public Action action() {
        String actionStr = ConfigStore.getInstance().get(ACTION.asNamed(prefix)).orElse("BLOCK");

        try {
            return Action.valueOf(actionStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Action.BLOCK;
        }
    }

    public String redactionText() {
        return ConfigStore.getInstance().get(REDACTION_TEXT.asNamed(prefix)).orElse("[REDACTED]");
    }
}
