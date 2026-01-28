package io.kaoto.forage.guardrails.input;

import dev.langchain4j.guardrail.InputGuardrail;
import io.kaoto.forage.core.annotations.ForageBean;
import io.kaoto.forage.core.guardrails.InputGuardrailProvider;
import java.util.Set;
import org.apache.camel.component.langchain4j.agent.api.guardrails.PiiDetectorGuardrail;
import org.apache.camel.component.langchain4j.agent.api.guardrails.PiiDetectorGuardrail.PiiType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating PiiDetectorGuardrail instances.
 *
 * <p>This guardrail detects Personally Identifiable Information (PII) in user messages,
 * such as email addresses, phone numbers, SSNs, credit card numbers, and IP addresses.
 *
 * <p>Configuration:
 * <ul>
 *   <li>forage.guardrail.pii.detect.types - PII types to detect (default: all)</li>
 *   <li>forage.guardrail.pii.block.on.detection - Block on detection (default: true)</li>
 * </ul>
 */
@ForageBean(
        value = "pii-detector",
        components = {"camel-langchain4j-agent"},
        feature = "Input Guardrail",
        description = "Detects PII (email, phone, SSN, credit card, IP address) in input messages")
public class PiiDetectorGuardrailProvider implements InputGuardrailProvider {

    private static final Logger LOG = LoggerFactory.getLogger(PiiDetectorGuardrailProvider.class);

    @Override
    public InputGuardrail create(String id) {
        final PiiDetectorGuardrailConfig config = new PiiDetectorGuardrailConfig(id);

        Set<PiiType> detectTypes = config.detectTypes();
        boolean blockOnDetection = config.blockOnDetection();

        LOG.trace(
                "Creating PiiDetectorGuardrail with detectTypes={}, blockOnDetection={}",
                detectTypes,
                blockOnDetection);

        return PiiDetectorGuardrail.builder()
                .detectTypes(detectTypes)
                .blockOnDetection(blockOnDetection)
                .build();
    }
}
