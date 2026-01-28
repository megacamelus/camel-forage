package io.kaoto.forage.guardrails.output;

import dev.langchain4j.guardrail.OutputGuardrail;
import io.kaoto.forage.core.annotations.ForageBean;
import io.kaoto.forage.core.guardrails.OutputGuardrailProvider;
import org.apache.camel.component.langchain4j.agent.api.guardrails.OutputLengthGuardrail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating OutputLengthGuardrail instances.
 *
 * <p>This guardrail validates the length of AI responses, ensuring they meet
 * length requirements for consistent response formats.
 *
 * <p>Configuration:
 * <ul>
 *   <li>forage.guardrail.output.length.max.chars - Maximum allowed characters (default: 50000)</li>
 *   <li>forage.guardrail.output.length.min.chars - Minimum required characters (default: 1)</li>
 *   <li>forage.guardrail.output.length.truncate.on.overflow - Truncate instead of failing (default: false)</li>
 * </ul>
 */
@ForageBean(
        value = "output-length",
        components = {"camel-langchain4j-agent"},
        feature = "Output Guardrail",
        description = "Validates output message length (min/max characters, optional truncation)")
public class OutputLengthGuardrailProvider implements OutputGuardrailProvider {

    private static final Logger LOG = LoggerFactory.getLogger(OutputLengthGuardrailProvider.class);

    @Override
    public OutputGuardrail create(String id) {
        final OutputLengthGuardrailConfig config = new OutputLengthGuardrailConfig(id);

        int maxChars = config.maxChars();
        int minChars = config.minChars();
        boolean truncateOnOverflow = config.truncateOnOverflow();

        LOG.trace(
                "Creating OutputLengthGuardrail with maxChars={}, minChars={}, truncateOnOverflow={}",
                maxChars,
                minChars,
                truncateOnOverflow);

        return OutputLengthGuardrail.builder()
                .maxChars(maxChars)
                .minChars(minChars)
                .truncateOnOverflow(truncateOnOverflow)
                .build();
    }
}
