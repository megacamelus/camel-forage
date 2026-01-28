package io.kaoto.forage.guardrails.output;

import dev.langchain4j.guardrail.OutputGuardrail;
import io.kaoto.forage.core.annotations.ForageBean;
import io.kaoto.forage.core.guardrails.OutputGuardrailProvider;
import java.util.Arrays;
import org.apache.camel.component.langchain4j.agent.api.guardrails.JsonFormatGuardrail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating JsonFormatGuardrail instances.
 *
 * <p>This guardrail validates that AI responses are valid JSON format,
 * optionally checking for required fields.
 *
 * <p>Configuration:
 * <ul>
 *   <li>forage.guardrail.json.format.required.fields - Comma-separated required fields</li>
 *   <li>forage.guardrail.json.format.extract.json - Extract JSON from text (default: true)</li>
 *   <li>forage.guardrail.json.format.allow.array - Allow JSON arrays (default: true)</li>
 * </ul>
 */
@ForageBean(
        value = "json-format",
        components = {"camel-langchain4j-agent"},
        feature = "Output Guardrail",
        description = "Validates output is valid JSON format with optional required fields")
public class JsonFormatGuardrailProvider implements OutputGuardrailProvider {

    private static final Logger LOG = LoggerFactory.getLogger(JsonFormatGuardrailProvider.class);

    @Override
    public OutputGuardrail create(String id) {
        final JsonFormatGuardrailConfig config = new JsonFormatGuardrailConfig(id);

        String[] requiredFields = config.requiredFields();
        boolean extractJson = config.extractJson();
        boolean allowArray = config.allowArray();

        LOG.trace(
                "Creating JsonFormatGuardrail with requiredFields={}, extractJson={}, allowArray={}",
                Arrays.toString(requiredFields),
                extractJson,
                allowArray);

        return JsonFormatGuardrail.builder()
                .requireFields(requiredFields)
                .extractJson(extractJson)
                .allowArray(allowArray)
                .build();
    }
}
