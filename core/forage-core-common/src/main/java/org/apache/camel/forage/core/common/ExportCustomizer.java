package org.apache.camel.forage.core.common;

import java.util.Set;

/**
 * Kind of SPI interface for enhancing exports using <strong>camel forage export (or run)</strong>
 *
 * <p>
 * The ony ability is to add runtime based dependencies.
 *
 */
public interface ExportCustomizer {

    /**
     * Each customizer is used for resolving dependencies during every export/run action.
     * It might be beneficial, to skip the exporter if obviously should be skipped
     * (like missing mandatory parameter or none parameter at all)
     *
     * @return true, if exporter should be executed.
     */
    boolean isEnabled();

    /**
     * Exporter is responsible for gathering all dependencies required for export/run action.
     *
     * @param runtime camel-main || spring-boot || quarkus
     * @return Set of all required dependencies.
     */
    Set<String> resolveRuntimeDependencies(RuntimeType runtime);
}
