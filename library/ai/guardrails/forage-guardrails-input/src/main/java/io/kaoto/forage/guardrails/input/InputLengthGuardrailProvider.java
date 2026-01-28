package io.kaoto.forage.guardrails.input;

import dev.langchain4j.guardrail.InputGuardrail;
import io.kaoto.forage.core.annotations.ForageBean;
import io.kaoto.forage.core.guardrails.InputGuardrailProvider;
import org.apache.camel.component.langchain4j.agent.api.guardrails.InputLengthGuardrail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating InputLengthGuardrail instances.
 *
 * <p>This guardrail validates the length of user messages, preventing excessively
 * long messages from being sent to the LLM.
 *
 * <p>Configuration:
 * <ul>
 *   <li>forage.guardrail.input.length.max.chars - Maximum allowed characters (default: 10000)</li>
 *   <li>forage.guardrail.input.length.min.chars - Minimum required characters (default: 1)</li>
 * </ul>
 */
@ForageBean(
        value = "input-length",
        components = {"camel-langchain4j-agent"},
        feature = "Input Guardrail",
        description = "Validates input message length (min/max characters)")
public class InputLengthGuardrailProvider implements InputGuardrailProvider {

    private static final Logger LOG = LoggerFactory.getLogger(InputLengthGuardrailProvider.class);

    @Override
    public InputGuardrail create(String id) {
        final InputLengthGuardrailConfig config = new InputLengthGuardrailConfig(id);

        int maxChars = config.maxChars();
        int minChars = config.minChars();

        LOG.trace("Creating InputLengthGuardrail with maxChars={}, minChars={}", maxChars, minChars);

        return InputLengthGuardrail.create(maxChars, minChars);
    }
}
