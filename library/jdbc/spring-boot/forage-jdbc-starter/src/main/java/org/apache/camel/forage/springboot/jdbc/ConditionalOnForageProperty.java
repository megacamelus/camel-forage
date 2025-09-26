package org.apache.camel.forage.springboot.jdbc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.camel.forage.core.util.config.Config;
import org.springframework.context.annotation.Conditional;

/**
 * {@link Conditional @Conditional} that checks if a Forage configuration property
 * matches the expected value. Conditionally enables Spring components based on
 * Forage configuration properties in any configuration (default or prefixed).
 *
 * <p>Example usage:
 * <pre class="code">
 * // Check if transaction is enabled in DataSourceFactoryConfig
 * &#64;Configuration
 * &#64;ConditionalOnForageProperty(
 *     configClass = DataSourceFactoryConfig.class,
 *     property = "jdbc.transaction.enabled",
 *     havingValue = "true"
 * )
 * &#64;EnableTransactionManagement
 * public class TransactionConfiguration {
 *     // Configuration beans that should only exist when transactions are enabled
 * }
 * </pre>
 *
 * @see ForagePropertyCondition
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ForagePropertyCondition.class)
public @interface ConditionalOnForageProperty {

    /**
     * The Forage configuration class that contains the property to check.
     * Must implement {@link Config}.
     *
     * @return the configuration class
     */
    Class<? extends Config> configClass();

    /**
     * The property name as defined in the configuration's ConfigEntries.
     * This should match the property name used in ConfigModule.of() calls.
     * For example: "jdbc.transaction.enabled" for DataSourceFactoryConfig.
     *
     * @return the property name
     */
    String property();

    /**
     * The expected value for the property. If not specified, the condition
     * will match if the property exists and is not null/empty.
     *
     * <p>For boolean properties, use "true" or "false".
     * For other types, the string representation will be compared.
     *
     * @return the expected value
     */
    String havingValue() default "";

    /**
     * Whether to match when the property does NOT equal the havingValue.
     * When true, the condition matches when the property value is different
     * from havingValue (or when havingValue is not specified, when the
     * property is null/empty).
     *
     * @return true to negate the condition
     */
    boolean matchIfMissing() default false;
}
