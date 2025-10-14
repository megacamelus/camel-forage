package org.apache.camel.forage.core.common;

import java.util.Locale;

/**
 * Enum with supported runtimes. Duplicates on org.apache.camel.dsl.jbang.core.common.RuntimeType, but allows us to not
 * depend on cameljbang here, in commons module.
 */
public enum RuntimeType {
    main,
    quarkus,
    springBoot;

    public static RuntimeType fromValue(String value) {
        value = value.toLowerCase(Locale.ROOT);
        return switch (value) {
            case "springBoot", "spring-boot", "camel-spring-boot" -> RuntimeType.springBoot;
            case "quarkus", "camel-quarkus" -> RuntimeType.quarkus;
            case "main", "camel-main" -> RuntimeType.main;
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
