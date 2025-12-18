package org.apache.camel.forage.core.annotations;

/**
 * Enumeration of supported factory variants for different runtime platforms.
 */
public enum FactoryVariant {
    /** Base/standalone Camel runtime */
    BASE("base"),

    /** Spring Boot runtime */
    SPRING_BOOT("springboot"),

    /** Quarkus runtime */
    QUARKUS("quarkus");

    private final String key;

    FactoryVariant(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
