package io.kaoto.forage.core.util.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ConfigModuleTest {

    private class TestConfig implements Config {

        @Override
        public String name() {
            return "test-config";
        }

        @Override
        public void register(String name, String value) {
            // NO-OP
        }
    }

    @Test
    void asNamed() {
        final ConfigModule configModule = ConfigModule.of(TestConfig.class, "test.config");
        assertEquals("test.config", configModule.name());

        final ConfigModule configModulePrefixed = configModule.asNamed("prefix");
        assertEquals("prefix.test.config", configModulePrefixed.name());

        final ConfigModule configModulePrefixed2 = configModulePrefixed.asNamed("prefix2");
        assertEquals("prefix2.test.config", configModulePrefixed2.name());
    }

    @Test
    void match() {
        final ConfigModule configModule = ConfigModule.of(TestConfig.class, "test.config");
        assertTrue(configModule.match("test.config"));

        final ConfigModule configModulePrefixed = configModule.asNamed("prefix");
        assertFalse(configModulePrefixed.match("test.config"));
        assertTrue(configModulePrefixed.match("prefix.test.config"));

        final ConfigModule configModulePrefixed2 = configModulePrefixed.asNamed("prefix2");
        assertFalse(configModulePrefixed2.match("test.config"));
        assertTrue(configModulePrefixed2.match("prefix2.test.config"));
    }
}
