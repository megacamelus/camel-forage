package org.apache.camel.forage.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark classes as Forage beans.
 *
 * This annotation can be used to identify and configure Forage components
 * for automatic discovery and registration.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ForageBean {

    /**
     * The name of the bean. If not specified, the class name will be used.
     *
     * @return the bean name
     */
    String value() default "";

    /**
     * The component that this Forage bean supports (e.g., "camel-langchain4j-agent", "camel-langchain4j-embeddings").
     *
     * @return the supported component
     */
    String[] components() default {};

    /**
     * Description of what this bean provides.
     *
     * @return the bean description
     */
    String description() default "";
}
