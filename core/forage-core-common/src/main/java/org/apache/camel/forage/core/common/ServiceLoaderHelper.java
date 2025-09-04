package org.apache.camel.forage.core.common;

import java.util.List;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for working with Java's ServiceLoader mechanism in the Camel Forage framework.
 *
 * <p>This helper class provides convenient methods for discovering and working with service providers
 * loaded through Java's ServiceLoader SPI (Service Provider Interface). It's primarily used by
 * the framework to locate and instantiate various components such as model providers, memory
 * factories, and other pluggable services.
 *
 * <p><strong>Key Features:</strong>
 * <ul>
 *   <li>Provider discovery by class name</li>
 *   <li>Type-safe service provider lookup</li>
 *   <li>Logging support for debugging service discovery issues</li>
 *   <li>Integration with the Camel Forage component architecture</li>
 * </ul>
 *
 * <p><strong>Usage Pattern:</strong>
 * This class is typically used by factory classes and component discoverers to find specific
 * implementations of services. For example, finding a specific model provider by its class name
 * from a list of available providers discovered via ServiceLoader.
 *
 * <p><strong>ServiceLoader Integration:</strong>
 * The Camel Forage framework uses ServiceLoader for automatic discovery of:
 * <ul>
 *   <li>Model providers ({@code org.apache.camel.forage.core.ai.ModelProvider})</li>
 *   <li>Memory factories ({@code org.apache.camel.forage.core.ai.ChatMemoryFactory})</li>
 *   <li>Vector database providers ({@code org.apache.camel.forage.core.vectordb.EmbeddingStoreProvider})</li>
 *   <li>Agent implementations ({@code org.apache.camel.component.langchain4j.agent.api.Agent})</li>
 * </ul>
 *
 * <p><strong>Thread Safety:</strong>
 * This utility class is stateless and thread-safe. All methods are static and can be called
 * concurrently from multiple threads without synchronization.
 *
 * @since 1.0
 */
public class ServiceLoaderHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceLoaderHelper.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ServiceLoaderHelper() {}

    /**
     * Finds a service provider by its class name from a list of available providers.
     *
     * <p>This method searches through a list of ServiceLoader providers to find one whose
     * service type matches the specified class name. It performs an exact string match
     * on the fully qualified class name.
     *
     * <p><strong>Usage Example:</strong>
     * <pre>{@code
     * ServiceLoader<ModelProvider> loader = ServiceLoader.load(ModelProvider.class);
     * List<ServiceLoader.Provider<ModelProvider>> providers = loader.stream().toList();
     * ServiceLoader.Provider<ModelProvider> openAiProvider =
     *     ServiceLoaderHelper.findProviderByClassName(providers,
     *         "org.apache.camel.forage.models.chat.openai.OpenAIProvider");
     * }</pre>
     *
     * <p><strong>Logging:</strong>
     * This method logs each comparison attempt at INFO level, which is useful for debugging
     * service discovery issues and understanding which providers are being evaluated.
     *
     * @param <T> the service type
     * @param providers the list of service providers to search through
     * @param name the fully qualified class name to search for
     * @return the matching service provider, or {@code null} if no match is found
     *
     * @throws NullPointerException if providers list is null
     * @throws IllegalArgumentException if name is null or empty
     */
    public static <T> ServiceLoader.Provider<T> findProviderByClassName(
            List<ServiceLoader.Provider<T>> providers, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Provider class name cannot be null or empty");
        }

        final ServiceLoader.Provider<T> provider =
                providers.stream().filter(p -> isEquals(name, p)).findFirst().orElse(null);

        return provider;
    }

    /**
     * Checks if a service provider's type name equals the specified name.
     *
     * <p>This method performs the actual comparison between the target class name and
     * the provider's service type name. It also logs the comparison attempt for debugging
     * purposes, showing which provider is being checked against which target name.
     *
     * <p><strong>Comparison Logic:</strong>
     * The comparison is performed using exact string equality on the fully qualified
     * class names. No partial matching or wildcard support is provided.
     *
     * @param <T> the service type
     * @param name the target class name to match against
     * @param p the service provider to check
     * @return {@code true} if the provider's type name exactly matches the target name,
     *         {@code false} otherwise
     */
    private static <T> boolean isEquals(String name, ServiceLoader.Provider<T> p) {
        LOG.info(
                "Checking if {} provider for {} equals to {}",
                name,
                p.getClass().getName(),
                p.type().getName());
        return p.type().getName().equals(name);
    }
}
