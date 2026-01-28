package io.kaoto.forage.core.guardrails;

import dev.langchain4j.guardrail.OutputGuardrail;
import io.kaoto.forage.core.common.BeanProvider;

/**
 * Provider interface for creating output guardrails.
 *
 * <p>Output guardrails validate AI-generated responses before they are returned to the user,
 * detecting and potentially redacting sensitive information, enforcing format requirements, etc.
 *
 * @see dev.langchain4j.guardrail.OutputGuardrail
 */
public interface OutputGuardrailProvider extends BeanProvider<OutputGuardrail> {}
