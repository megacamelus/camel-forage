package org.apache.camel.forage.core.util.config;

import java.util.Locale;

public enum RuntimeType {
    springBoot,
    quarkus,
    main;

    public static RuntimeType fromValue(String value) {
        value = value.toLowerCase(Locale.ROOT);
        return switch (value) {
            case "spring", "spring-boot", "camel-spring-boot" -> RuntimeType.springBoot;
            case "quarkus", "camel-quarkus" -> RuntimeType.quarkus;
            case "main", "camel-main", "camel" -> RuntimeType.main;
            default -> throw new IllegalArgumentException("Unsupported runtime " + value);
        };
    }

    public String runtime() {
        return switch (this) {
            case springBoot -> "spring-boot";
            case quarkus -> "quarkus";
            case main -> "main";
        };
    }

    @Override
    public String toString() {
        return runtime();
    }
}
