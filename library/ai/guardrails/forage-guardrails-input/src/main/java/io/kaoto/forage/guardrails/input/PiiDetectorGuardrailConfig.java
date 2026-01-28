package io.kaoto.forage.guardrails.input;

import static io.kaoto.forage.guardrails.input.PiiDetectorGuardrailConfigEntries.*;

import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import org.apache.camel.component.langchain4j.agent.api.guardrails.PiiDetectorGuardrail.PiiType;

/**
 * Configuration class for PII detector guardrail.
 */
public class PiiDetectorGuardrailConfig implements Config {

    private final String prefix;

    public PiiDetectorGuardrailConfig() {
        this(null);
    }

    public PiiDetectorGuardrailConfig(String prefix) {
        this.prefix = prefix;

        PiiDetectorGuardrailConfigEntries.register(prefix);
        ConfigStore.getInstance().load(PiiDetectorGuardrailConfig.class, this, this::register);
        PiiDetectorGuardrailConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = PiiDetectorGuardrailConfigEntries.find(prefix, name);
        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    @Override
    public String name() {
        return "forage-guardrail-pii-detector";
    }

    public Set<PiiType> detectTypes() {
        String typesStr = ConfigStore.getInstance()
                .get(DETECT_TYPES.asNamed(prefix))
                .orElse("EMAIL,PHONE,SSN,CREDIT_CARD,IP_ADDRESS");

        Set<PiiType> types = EnumSet.noneOf(PiiType.class);
        Arrays.stream(typesStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(s -> {
                    try {
                        types.add(PiiType.valueOf(s.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        // Ignore invalid types
                    }
                });

        return types.isEmpty() ? EnumSet.allOf(PiiType.class) : types;
    }

    public boolean blockOnDetection() {
        return ConfigStore.getInstance()
                .get(BLOCK_ON_DETECTION.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(true);
    }
}
