package io.kaoto.forage.guardrails.input;

import dev.langchain4j.guardrail.InputGuardrail;
import io.kaoto.forage.core.annotations.ForageBean;
import io.kaoto.forage.core.guardrails.InputGuardrailProvider;
import org.apache.camel.component.langchain4j.agent.api.guardrails.PromptInjectionGuardrail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating PromptInjectionGuardrail instances.
 *
 * <p>This guardrail detects potential prompt injection attacks, including:
 * <ul>
 *   <li>Instructions to ignore previous prompts</li>
 *   <li>Role manipulation attempts</li>
 *   <li>System prompt override attempts</li>
 *   <li>Jailbreak patterns</li>
 *   <li>Delimiter injection</li>
 * </ul>
 *
 * <p>Configuration:
 * <ul>
 *   <li>forage.guardrail.prompt.injection.strict - Enable strict mode (default: false)</li>
 * </ul>
 */
@ForageBean(
        value = "prompt-injection",
        components = {"camel-langchain4j-agent"},
        feature = "Input Guardrail",
        description = "Detects prompt injection attacks (role manipulation, jailbreak, etc.)")
public class PromptInjectionGuardrailProvider implements InputGuardrailProvider {

    private static final Logger LOG = LoggerFactory.getLogger(PromptInjectionGuardrailProvider.class);

    @Override
    public InputGuardrail create(String id) {
        final PromptInjectionGuardrailConfig config = new PromptInjectionGuardrailConfig(id);

        boolean strict = config.strict();

        LOG.trace("Creating PromptInjectionGuardrail with strict={}", strict);

        if (strict) {
            return PromptInjectionGuardrail.strict();
        }
        return new PromptInjectionGuardrail();
    }
}
