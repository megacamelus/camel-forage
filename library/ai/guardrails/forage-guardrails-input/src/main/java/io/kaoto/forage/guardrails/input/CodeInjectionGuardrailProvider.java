package io.kaoto.forage.guardrails.input;

import dev.langchain4j.guardrail.InputGuardrail;
import io.kaoto.forage.core.annotations.ForageBean;
import io.kaoto.forage.core.guardrails.InputGuardrailProvider;
import java.util.Set;
import org.apache.camel.component.langchain4j.agent.api.guardrails.CodeInjectionGuardrail;
import org.apache.camel.component.langchain4j.agent.api.guardrails.CodeInjectionGuardrail.InjectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating CodeInjectionGuardrail instances.
 *
 * <p>This guardrail detects potential code injection attempts, including:
 * <ul>
 *   <li>Shell command injection</li>
 *   <li>SQL injection patterns</li>
 *   <li>JavaScript/HTML injection</li>
 *   <li>Path traversal attempts</li>
 *   <li>Command chaining patterns</li>
 *   <li>Template injection</li>
 * </ul>
 *
 * <p>Configuration:
 * <ul>
 *   <li>forage.guardrail.code.injection.strict - Enable strict mode (default: false)</li>
 *   <li>forage.guardrail.code.injection.detect.types - Types to detect (default: all)</li>
 * </ul>
 */
@ForageBean(
        value = "code-injection",
        components = {"camel-langchain4j-agent"},
        feature = "Input Guardrail",
        description = "Detects code injection attacks (SQL, shell, XSS, path traversal, etc.)")
public class CodeInjectionGuardrailProvider implements InputGuardrailProvider {

    private static final Logger LOG = LoggerFactory.getLogger(CodeInjectionGuardrailProvider.class);

    @Override
    public InputGuardrail create(String id) {
        final CodeInjectionGuardrailConfig config = new CodeInjectionGuardrailConfig(id);

        boolean strict = config.strict();
        Set<InjectionType> detectTypes = config.detectTypes();

        LOG.trace("Creating CodeInjectionGuardrail with strict={}, detectTypes={}", strict, detectTypes);

        return CodeInjectionGuardrail.builder()
                .strict(strict)
                .detectTypes(detectTypes.toArray(new InjectionType[0]))
                .build();
    }
}
