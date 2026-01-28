package io.kaoto.forage.guardrails.output;

import dev.langchain4j.guardrail.OutputGuardrail;
import io.kaoto.forage.core.annotations.ForageBean;
import io.kaoto.forage.core.guardrails.OutputGuardrailProvider;
import java.util.Set;
import org.apache.camel.component.langchain4j.agent.api.guardrails.SensitiveDataOutputGuardrail;
import org.apache.camel.component.langchain4j.agent.api.guardrails.SensitiveDataOutputGuardrail.Action;
import org.apache.camel.component.langchain4j.agent.api.guardrails.SensitiveDataOutputGuardrail.SensitiveDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating SensitiveDataOutputGuardrail instances.
 *
 * <p>This guardrail detects and optionally redacts sensitive data in AI responses,
 * such as API keys, passwords, credit card numbers, and other secrets.
 *
 * <p>Configuration:
 * <ul>
 *   <li>forage.guardrail.sensitive.data.detect.types - Types to detect (default: all)</li>
 *   <li>forage.guardrail.sensitive.data.action - BLOCK, REDACT, or WARN (default: BLOCK)</li>
 *   <li>forage.guardrail.sensitive.data.redaction.text - Redaction text (default: [REDACTED])</li>
 * </ul>
 */
@ForageBean(
        value = "sensitive-data",
        components = {"camel-langchain4j-agent"},
        feature = "Output Guardrail",
        description = "Detects and optionally redacts sensitive data (API keys, secrets, PII) in output")
public class SensitiveDataGuardrailProvider implements OutputGuardrailProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SensitiveDataGuardrailProvider.class);

    @Override
    public OutputGuardrail create(String id) {
        final SensitiveDataGuardrailConfig config = new SensitiveDataGuardrailConfig(id);

        Set<SensitiveDataType> detectTypes = config.detectTypes();
        Action action = config.action();
        String redactionText = config.redactionText();

        LOG.trace(
                "Creating SensitiveDataOutputGuardrail with detectTypes={}, action={}, redactionText={}",
                detectTypes,
                action,
                redactionText);

        return SensitiveDataOutputGuardrail.builder()
                .detectTypes(detectTypes.toArray(new SensitiveDataType[0]))
                .action(action)
                .redactionText(redactionText)
                .build();
    }
}
