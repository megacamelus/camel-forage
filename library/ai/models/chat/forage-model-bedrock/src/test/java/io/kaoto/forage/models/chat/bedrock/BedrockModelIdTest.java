package io.kaoto.forage.models.chat.bedrock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for BedrockModelId enum.
 */
@DisplayName("BedrockModelId Enum Tests")
class BedrockModelIdTest {

    @Test
    @DisplayName("Should correctly identify Claude models")
    void shouldCorrectlyIdentifyClaudeModels() {
        assertThat(BedrockModelId.CLAUDE_3_5_SONNET.isClaude()).isTrue();
        assertThat(BedrockModelId.CLAUDE_3_OPUS.isClaude()).isTrue();
        assertThat(BedrockModelId.CLAUDE_3_SONNET.isClaude()).isTrue();
        assertThat(BedrockModelId.CLAUDE_3_HAIKU.isClaude()).isTrue();
        assertThat(BedrockModelId.CLAUDE_2_1.isClaude()).isTrue();
        assertThat(BedrockModelId.CLAUDE_2.isClaude()).isTrue();

        assertThat(BedrockModelId.LLAMA_3_1_70B.isClaude()).isFalse();
        assertThat(BedrockModelId.TITAN_TEXT_EXPRESS.isClaude()).isFalse();
    }

    @Test
    @DisplayName("Should correctly identify Llama models")
    void shouldCorrectlyIdentifyLlamaModels() {
        assertThat(BedrockModelId.LLAMA_3_1_405B.isLlama()).isTrue();
        assertThat(BedrockModelId.LLAMA_3_1_70B.isLlama()).isTrue();
        assertThat(BedrockModelId.LLAMA_3_1_8B.isLlama()).isTrue();
        assertThat(BedrockModelId.LLAMA_3_70B.isLlama()).isTrue();
        assertThat(BedrockModelId.LLAMA_3_8B.isLlama()).isTrue();
        assertThat(BedrockModelId.LLAMA_2_70B.isLlama()).isTrue();
        assertThat(BedrockModelId.LLAMA_2_13B.isLlama()).isTrue();

        assertThat(BedrockModelId.CLAUDE_3_HAIKU.isLlama()).isFalse();
        assertThat(BedrockModelId.TITAN_TEXT_EXPRESS.isLlama()).isFalse();
    }

    @Test
    @DisplayName("Should correctly identify Titan models")
    void shouldCorrectlyIdentifyTitanModels() {
        assertThat(BedrockModelId.TITAN_TEXT_PREMIER.isTitan()).isTrue();
        assertThat(BedrockModelId.TITAN_TEXT_EXPRESS.isTitan()).isTrue();
        assertThat(BedrockModelId.TITAN_TEXT_LITE.isTitan()).isTrue();

        assertThat(BedrockModelId.CLAUDE_3_HAIKU.isTitan()).isFalse();
        assertThat(BedrockModelId.LLAMA_3_1_70B.isTitan()).isFalse();
    }

    @Test
    @DisplayName("Should correctly identify Cohere models")
    void shouldCorrectlyIdentifyCohereModels() {
        assertThat(BedrockModelId.COMMAND_R_PLUS.isCohere()).isTrue();
        assertThat(BedrockModelId.COMMAND_R.isCohere()).isTrue();
        assertThat(BedrockModelId.COMMAND_TEXT.isCohere()).isTrue();
        assertThat(BedrockModelId.COMMAND_LIGHT_TEXT.isCohere()).isTrue();

        assertThat(BedrockModelId.CLAUDE_3_HAIKU.isCohere()).isFalse();
        assertThat(BedrockModelId.LLAMA_3_1_70B.isCohere()).isFalse();
    }

    @Test
    @DisplayName("Should correctly identify Mistral models")
    void shouldCorrectlyIdentifyMistralModels() {
        assertThat(BedrockModelId.MISTRAL_LARGE.isMistral()).isTrue();
        assertThat(BedrockModelId.MISTRAL_7B.isMistral()).isTrue();
        assertThat(BedrockModelId.MIXTRAL_8X7B.isMistral()).isTrue();

        assertThat(BedrockModelId.CLAUDE_3_HAIKU.isMistral()).isFalse();
        assertThat(BedrockModelId.LLAMA_3_1_70B.isMistral()).isFalse();
    }

    @Test
    @DisplayName("Should have correct model IDs")
    void shouldHaveCorrectModelIds() {
        assertThat(BedrockModelId.CLAUDE_3_5_SONNET.getModelId())
                .isEqualTo("anthropic.claude-3-5-sonnet-20240620-v1:0");
        assertThat(BedrockModelId.LLAMA_3_1_70B.getModelId()).isEqualTo("meta.llama3-1-70b-instruct-v1:0");
        assertThat(BedrockModelId.TITAN_TEXT_EXPRESS.getModelId()).isEqualTo("amazon.titan-text-express-v1");
        assertThat(BedrockModelId.COMMAND_R_PLUS.getModelId()).isEqualTo("cohere.command-r-plus-v1:0");
        assertThat(BedrockModelId.MISTRAL_LARGE.getModelId()).isEqualTo("mistral.mistral-large-2402-v1:0");
    }

    @Test
    @DisplayName("Should have display names")
    void shouldHaveDisplayNames() {
        assertThat(BedrockModelId.CLAUDE_3_5_SONNET.getDisplayName()).isEqualTo("Claude 3.5 Sonnet");
        assertThat(BedrockModelId.LLAMA_3_1_70B.getDisplayName()).isEqualTo("Llama 3.1 70B Instruct");
        assertThat(BedrockModelId.TITAN_TEXT_EXPRESS.getDisplayName()).isEqualTo("Titan Text Express");
        assertThat(BedrockModelId.COMMAND_R_PLUS.getDisplayName()).isEqualTo("Command R+");
        assertThat(BedrockModelId.MISTRAL_LARGE.getDisplayName()).isEqualTo("Mistral Large");
    }

    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        String toString = BedrockModelId.CLAUDE_3_5_SONNET.toString();
        assertThat(toString).contains("Claude 3.5 Sonnet");
        assertThat(toString).contains("anthropic.claude-3-5-sonnet-20240620-v1:0");
    }
}
