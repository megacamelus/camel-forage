package org.apache.camel.forage.core.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark classes as Forage factories.
 *
 * This annotation can be used to identify and configure Forage factory components
 * for automatic discovery and registration. Factories are responsible for creating
 * and configuring other Forage components.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ForageFactory {

    /**
     * The name of the factory. If not specified, the class name will be used.
     *
     * @return the factory name
     */
    String value() default "";

    /**
     * The component that this Forage factory supports (e.g., "camel-langchain4j-agent", "camel-langchain4j-embeddings").
     *
     * @return the supported component
     */
    String[] components() default {};

    /**
     * Description of what this factory provides.
     *
     * @return the factory description
     */
    String description() default "";

    /**
     * The type of objects this factory creates (e.g., "Agent", "ChatModel", "EmbeddingStore").
     *
     * @return the factory type
     */
    String factoryType() default "";

    /**
     * Mark if the Factory configure itself, without having to configure the Factory name on the component.
     *
     * @return
     */
    boolean autowired() default false;
}
