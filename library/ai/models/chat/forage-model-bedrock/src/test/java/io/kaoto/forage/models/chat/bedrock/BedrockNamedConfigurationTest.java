package io.kaoto.forage.models.chat.bedrock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for BedrockConfig focusing on named/prefixed configuration support.
 */
@DisplayName("BedrockConfig Named Configuration Tests")
class BedrockNamedConfigurationTest {

    @AfterEach
    void cleanup() {
        System.clearProperty("forage.bedrock.model.id");
        System.clearProperty("forage.bedrock.region");
        System.clearProperty("forage.bedrock.temperature");
        System.clearProperty("forage.bedrock.access.key.id");
        System.clearProperty("forage.bedrock.secret.access.key");
        System.clearProperty("forage.agent1.bedrock.model.id");
        System.clearProperty("forage.agent2.bedrock.model.id");
        System.clearProperty("forage.eu.bedrock.region");
        System.clearProperty("forage.asia.bedrock.region");
        System.clearProperty("forage.creative.bedrock.temperature");
        System.clearProperty("forage.precise.bedrock.temperature");
        System.clearProperty("forage.prod.bedrock.access.key.id");
        System.clearProperty("forage.prod.bedrock.secret.access.key");
        System.clearProperty("forage.multi1.bedrock.model.id");
        System.clearProperty("forage.multi2.bedrock.model.id");
        System.clearProperty("forage.multi2.bedrock.temperature");
        System.clearProperty("forage.multi3.bedrock.model.id");
        System.clearProperty("forage.multi3.bedrock.temperature");
        System.clearProperty("forage.multi3.bedrock.max.tokens");
    }

    @Test
    @DisplayName("Should support named configurations with different model IDs")
    void shouldSupportNamedConfigurationsWithDifferentModelIds() {
        System.setProperty("forage.bedrock.model.id", "anthropic.claude-3-haiku-20240307-v1:0");
        System.setProperty("forage.agent1.bedrock.model.id", "anthropic.claude-3-5-sonnet-20240620-v1:0");
        System.setProperty("forage.agent2.bedrock.model.id", "meta.llama3-1-70b-instruct-v1:0");

        BedrockConfig defaultConfig = new BedrockConfig();
        BedrockConfig agent1Config = new BedrockConfig("agent1");
        BedrockConfig agent2Config = new BedrockConfig("agent2");

        assertThat(defaultConfig.modelId()).isEqualTo("anthropic.claude-3-haiku-20240307-v1:0");
        assertThat(agent1Config.modelId()).isEqualTo("anthropic.claude-3-5-sonnet-20240620-v1:0");
        assertThat(agent2Config.modelId()).isEqualTo("meta.llama3-1-70b-instruct-v1:0");
    }

    @Test
    @DisplayName("Should support named configurations with different regions")
    void shouldSupportNamedConfigurationsWithDifferentRegions() {
        System.setProperty("forage.bedrock.region", "us-east-1");
        System.setProperty("forage.eu.bedrock.region", "eu-central-1");
        System.setProperty("forage.asia.bedrock.region", "ap-southeast-1");

        BedrockConfig defaultConfig = new BedrockConfig();
        BedrockConfig euConfig = new BedrockConfig("eu");
        BedrockConfig asiaConfig = new BedrockConfig("asia");

        assertThat(defaultConfig.region()).isEqualTo("us-east-1");
        assertThat(euConfig.region()).isEqualTo("eu-central-1");
        assertThat(asiaConfig.region()).isEqualTo("ap-southeast-1");
    }

    @Test
    @DisplayName("Should support named configurations with different temperatures")
    void shouldSupportNamedConfigurationsWithDifferentTemperatures() {
        System.setProperty("forage.bedrock.temperature", "0.5");
        System.setProperty("forage.creative.bedrock.temperature", "0.9");
        System.setProperty("forage.precise.bedrock.temperature", "0.1");

        BedrockConfig defaultConfig = new BedrockConfig();
        BedrockConfig creativeConfig = new BedrockConfig("creative");
        BedrockConfig preciseConfig = new BedrockConfig("precise");

        assertThat(defaultConfig.temperature()).isEqualTo(0.5);
        assertThat(creativeConfig.temperature()).isEqualTo(0.9);
        assertThat(preciseConfig.temperature()).isEqualTo(0.1);
    }

    @Test
    @DisplayName("Should support named configurations with different AWS credentials")
    void shouldSupportNamedConfigurationsWithDifferentAwsCredentials() {
        System.setProperty("forage.bedrock.access.key.id", "DEFAULT_KEY");
        System.setProperty("forage.bedrock.secret.access.key", "DEFAULT_SECRET");
        System.setProperty("forage.prod.bedrock.access.key.id", "PROD_KEY");
        System.setProperty("forage.prod.bedrock.secret.access.key", "PROD_SECRET");

        BedrockConfig defaultConfig = new BedrockConfig();
        BedrockConfig prodConfig = new BedrockConfig("prod");

        assertThat(defaultConfig.accessKeyId()).isEqualTo("DEFAULT_KEY");
        assertThat(defaultConfig.secretAccessKey()).isEqualTo("DEFAULT_SECRET");
        assertThat(prodConfig.accessKeyId()).isEqualTo("PROD_KEY");
        assertThat(prodConfig.secretAccessKey()).isEqualTo("PROD_SECRET");
    }

    @Test
    @DisplayName("Should support multiple named configurations independently")
    void shouldSupportMultipleNamedConfigurationsIndependently() {
        System.setProperty("forage.multi1.bedrock.model.id", "anthropic.claude-3-haiku-20240307-v1:0");
        System.setProperty("forage.multi2.bedrock.model.id", "anthropic.claude-3-haiku-20240307-v1:0");
        System.setProperty("forage.multi2.bedrock.temperature", "0.8");
        System.setProperty("forage.multi3.bedrock.model.id", "anthropic.claude-3-5-sonnet-20240620-v1:0");
        System.setProperty("forage.multi3.bedrock.temperature", "0.3");
        System.setProperty("forage.multi3.bedrock.max.tokens", "4096");

        BedrockConfig config1 = new BedrockConfig("multi1");
        BedrockConfig config2 = new BedrockConfig("multi2");
        BedrockConfig config3 = new BedrockConfig("multi3");

        assertThat(config1.modelId()).isEqualTo("anthropic.claude-3-haiku-20240307-v1:0");
        assertThat(config1.temperature()).isNull();
        assertThat(config1.maxTokens()).isNull();

        assertThat(config2.modelId()).isEqualTo("anthropic.claude-3-haiku-20240307-v1:0");
        assertThat(config2.temperature()).isEqualTo(0.8);
        assertThat(config2.maxTokens()).isNull();

        assertThat(config3.modelId()).isEqualTo("anthropic.claude-3-5-sonnet-20240620-v1:0");
        assertThat(config3.temperature()).isEqualTo(0.3);
        assertThat(config3.maxTokens()).isEqualTo(4096);
    }
}
