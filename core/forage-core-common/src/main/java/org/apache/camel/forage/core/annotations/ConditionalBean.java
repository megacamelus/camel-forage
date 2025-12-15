package org.apache.camel.forage.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a bean that is conditionally registered based on configuration.
 *
 * This annotation is used within {@link ConditionalBeans} to declare individual beans
 * that are automatically created when certain configuration options are enabled.
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface ConditionalBean {

    /**
     * The fixed bean name that will be registered in the Camel context.
     * Mutually exclusive with {@link #nameFromConfig()}.
     *
     * @return the bean name
     */
    String name() default "";

    /**
     * The config entry that provides the bean name dynamically.
     * Mutually exclusive with {@link #name()}.
     *
     * @return the config entry name that holds the bean name
     */
    String nameFromConfig() default "";

    /**
     * The Java type of the bean as a fully qualified class name.
     * This should be the interface or API type that users would reference,
     * not the internal implementation class.
     *
     * @return the fully qualified Java type name
     */
    String javaType();

    /**
     * Human-readable description of what this bean provides.
     *
     * @return the description
     */
    String description() default "";
}
