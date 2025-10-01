package org.apache.camel.forage.core.common;

/**
 * Enum with supported runtimes. Duplicates on org.apache.camel.dsl.jbang.core.common.RuntimeType, but allows us to not
 * depend on cameljbang here, in commons module.
 */
public enum RuntimeType {
    main,
    quarkus,
    springBoot;
}
