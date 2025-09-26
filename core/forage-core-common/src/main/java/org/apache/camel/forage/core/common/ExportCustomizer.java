package org.apache.camel.forage.core.common;

import java.util.Set;

/**
 * Kind of SPI interface for enhancing exports using <strong>camel forage export (or run)</strong>
 *
 * <p>
 * The ony ability is to add runtime based dependencies. See
 * {@link org.apache.camel.forage.jdbc.common.DatasourceExportCustomizer} for the usage
 * </p>
 */
public interface ExportCustomizer {

    default boolean isEnabled() {
        return true;
    }

    Set<String> resolveRuntimeDependencies(RuntimeType runtime);
}
