package io.kaoto.forage.guardrails.input;

import static io.kaoto.forage.guardrails.input.CodeInjectionGuardrailConfigEntries.*;

import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import org.apache.camel.component.langchain4j.agent.api.guardrails.CodeInjectionGuardrail.InjectionType;

/**
 * Configuration class for code injection guardrail.
 */
public class CodeInjectionGuardrailConfig implements Config {

    private final String prefix;

    public CodeInjectionGuardrailConfig() {
        this(null);
    }

    public CodeInjectionGuardrailConfig(String prefix) {
        this.prefix = prefix;

        CodeInjectionGuardrailConfigEntries.register(prefix);
        ConfigStore.getInstance().load(CodeInjectionGuardrailConfig.class, this, this::register);
        CodeInjectionGuardrailConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = CodeInjectionGuardrailConfigEntries.find(prefix, name);
        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    @Override
    public String name() {
        return "forage-guardrail-code-injection";
    }

    public boolean strict() {
        return ConfigStore.getInstance()
                .get(STRICT.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    public Set<InjectionType> detectTypes() {
        String typesStr = ConfigStore.getInstance()
                .get(DETECT_TYPES.asNamed(prefix))
                .orElse(
                        "SHELL_COMMAND,SQL_INJECTION,JAVASCRIPT,HTML_XSS,PATH_TRAVERSAL,COMMAND_CHAINING,TEMPLATE_INJECTION");

        Set<InjectionType> types = EnumSet.noneOf(InjectionType.class);
        Arrays.stream(typesStr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(s -> {
                    try {
                        types.add(InjectionType.valueOf(s.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        // Ignore invalid types
                    }
                });

        return types.isEmpty() ? EnumSet.allOf(InjectionType.class) : types;
    }
}
