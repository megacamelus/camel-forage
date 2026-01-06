package io.kaoto.forage.models.chat.bedrock;

/**
 * Enumeration of supported Amazon Bedrock foundation model identifiers.
 *
 * <p>This enum provides convenient access to the full model IDs required by Amazon Bedrock.
 * Model identifiers include version numbers to ensure compatibility and predictable behavior.
 *
 * <p><strong>Model Families:</strong>
 * <ul>
 *   <li><strong>Anthropic Claude</strong> - Advanced reasoning and analysis capabilities</li>
 *   <li><strong>Meta Llama</strong> - Open-source foundation models</li>
 *   <li><strong>Amazon Titan</strong> - AWS-native models optimized for various tasks</li>
 *   <li><strong>Cohere Command</strong> - Enterprise-focused language models</li>
 *   <li><strong>Mistral AI</strong> - High-performance European models</li>
 * </ul>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * System.setProperty("bedrock.model.id", BedrockModelId.CLAUDE_3_5_SONNET.getModelId());
 * }</pre>
 *
 * @since 1.0
 */
public enum BedrockModelId {

    // Anthropic Claude models
    CLAUDE_3_5_SONNET("anthropic.claude-3-5-sonnet-20240620-v1:0", "Claude 3.5 Sonnet"),
    CLAUDE_3_OPUS("anthropic.claude-3-opus-20240229-v1:0", "Claude 3 Opus"),
    CLAUDE_3_SONNET("anthropic.claude-3-sonnet-20240229-v1:0", "Claude 3 Sonnet"),
    CLAUDE_3_HAIKU("anthropic.claude-3-haiku-20240307-v1:0", "Claude 3 Haiku"),
    CLAUDE_2_1("anthropic.claude-v2:1", "Claude 2.1"),
    CLAUDE_2("anthropic.claude-v2", "Claude 2"),

    // Meta Llama models
    LLAMA_3_1_405B("meta.llama3-1-405b-instruct-v1:0", "Llama 3.1 405B Instruct"),
    LLAMA_3_1_70B("meta.llama3-1-70b-instruct-v1:0", "Llama 3.1 70B Instruct"),
    LLAMA_3_1_8B("meta.llama3-1-8b-instruct-v1:0", "Llama 3.1 8B Instruct"),
    LLAMA_3_70B("meta.llama3-70b-instruct-v1:0", "Llama 3 70B Instruct"),
    LLAMA_3_8B("meta.llama3-8b-instruct-v1:0", "Llama 3 8B Instruct"),
    LLAMA_2_70B("meta.llama2-70b-chat-v1", "Llama 2 70B Chat"),
    LLAMA_2_13B("meta.llama2-13b-chat-v1", "Llama 2 13B Chat"),

    // Amazon Titan models
    TITAN_TEXT_PREMIER("amazon.titan-text-premier-v1:0", "Titan Text Premier"),
    TITAN_TEXT_EXPRESS("amazon.titan-text-express-v1", "Titan Text Express"),
    TITAN_TEXT_LITE("amazon.titan-text-lite-v1", "Titan Text Lite"),

    // Cohere Command models
    COMMAND_R_PLUS("cohere.command-r-plus-v1:0", "Command R+"),
    COMMAND_R("cohere.command-r-v1:0", "Command R"),
    COMMAND_TEXT("cohere.command-text-v14", "Command Text"),
    COMMAND_LIGHT_TEXT("cohere.command-light-text-v14", "Command Light Text"),

    // Mistral AI models
    MISTRAL_LARGE("mistral.mistral-large-2402-v1:0", "Mistral Large"),
    MISTRAL_7B("mistral.mistral-7b-instruct-v0:2", "Mistral 7B Instruct"),
    MIXTRAL_8X7B("mistral.mixtral-8x7b-instruct-v0:1", "Mixtral 8x7B Instruct");

    private final String modelId;
    private final String displayName;

    BedrockModelId(String modelId, String displayName) {
        this.modelId = modelId;
        this.displayName = displayName;
    }

    /**
     * Returns the full Bedrock model identifier.
     *
     * @return the model ID string used by Bedrock API
     */
    public String getModelId() {
        return modelId;
    }

    /**
     * Returns the human-readable display name for the model.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if the model is from the Anthropic Claude family.
     *
     * @return true if this is a Claude model
     */
    public boolean isClaude() {
        return modelId.startsWith("anthropic.claude");
    }

    /**
     * Checks if the model is from the Meta Llama family.
     *
     * @return true if this is a Llama model
     */
    public boolean isLlama() {
        return modelId.startsWith("meta.llama");
    }

    /**
     * Checks if the model is from the Amazon Titan family.
     *
     * @return true if this is a Titan model
     */
    public boolean isTitan() {
        return modelId.startsWith("amazon.titan");
    }

    /**
     * Checks if the model is from the Cohere Command family.
     *
     * @return true if this is a Command model
     */
    public boolean isCohere() {
        return modelId.startsWith("cohere.command");
    }

    /**
     * Checks if the model is from the Mistral AI family.
     *
     * @return true if this is a Mistral model
     */
    public boolean isMistral() {
        return modelId.startsWith("mistral.");
    }

    @Override
    public String toString() {
        return displayName + " (" + modelId + ")";
    }
}
