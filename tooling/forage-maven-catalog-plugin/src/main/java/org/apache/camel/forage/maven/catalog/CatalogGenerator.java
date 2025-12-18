package org.apache.camel.forage.maven.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Core catalog generation logic for Camel Forage components.
 *
 * This class generates a simplified catalog structure for Kaoto integration,
 * grouping factories with their platform variants, beans by feature, and config entries.
 */
public class CatalogGenerator {

    private final Log log;
    private final ObjectMapper objectMapper;
    private final ObjectMapper yamlObjectMapper;

    private String format = "json";

    public CatalogGenerator(Log log) {
        this.log = log;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        this.yamlObjectMapper = new ObjectMapper(new YAMLFactory());
    }

    /**
     * Generate a catalog from the given Maven project.
     *
     * @param project the Maven project to scan
     * @param outputDirectory the directory to write catalog files
     * @return the catalog generation result
     * @throws IOException if file operations fail
     */
    public CatalogResult generateCatalog(MavenProject project, File outputDirectory) throws IOException {
        log.info("Scanning project dependencies for Forage components...");

        // Discover all components (old structure for gathering data)
        List<ForageComponent> components = discoverComponents(project);

        log.info("Found " + components.size() + " Forage components");

        // Transform to new simplified catalog structure
        List<ForageFactory> factories = transformToSimplifiedCatalog(components);

        log.info("Generated " + factories.size() + " factories in simplified catalog");

        // Create catalog data structure
        ForageCatalog catalog = new ForageCatalog();
        catalog.setVersion("2.0");
        catalog.setFactories(factories);
        catalog.setGeneratedBy("forage-maven-catalog-plugin");
        catalog.setTimestamp(System.currentTimeMillis());

        // Generate output files
        List<File> generatedFiles = new ArrayList<>();

        switch (format.toLowerCase()) {
            case "json":
                generatedFiles.add(generateJsonCatalog(catalog, outputDirectory));
                break;
            case "yaml":
                generatedFiles.add(generateYamlCatalog(catalog, outputDirectory));
                break;
            default:
                // Generate all formats
                generatedFiles.add(generateJsonCatalog(catalog, outputDirectory));
                generatedFiles.add(generateYamlCatalog(catalog, outputDirectory));
        }

        return new CatalogResult(factories.size(), generatedFiles);
    }

    /**
     * Transform legacy component-based structure to simplified factory-centric catalog.
     */
    private List<ForageFactory> transformToSimplifiedCatalog(List<ForageComponent> components) {
        // Collect all config mappings first (needed for other steps)
        Map<String, String> allConfigMappings = collectAllConfigMappings(components);
        log.info("Found " + allConfigMappings.size() + " Config classes total");

        // Collect data from components
        Map<String, ForageFactory> factoryMap = new LinkedHashMap<>();
        Map<String, List<ConfigEntry>> configsByPrefix = new HashMap<>();
        Map<String, List<BeanWithGav>> beansByComponent = new HashMap<>();

        collectDataFromComponents(components, factoryMap, configsByPrefix, beansByComponent, allConfigMappings);

        // Associate beans and configs with factories
        associateBeansWithFactories(factoryMap, beansByComponent, allConfigMappings);
        associateConfigEntriesWithFactories(factoryMap, configsByPrefix);

        return new ArrayList<>(factoryMap.values());
    }

    /**
     * Collects all Config class mappings from components.
     */
    private Map<String, String> collectAllConfigMappings(List<ForageComponent> components) {
        Map<String, String> allConfigMappings = new HashMap<>();
        for (ForageComponent component : components) {
            if (component.getConfigClasses() != null
                    && !component.getConfigClasses().isEmpty()) {
                allConfigMappings.putAll(component.getConfigClasses());
            }
        }
        return allConfigMappings;
    }

    /**
     * Collects factories, config entries, and beans from components.
     */
    private void collectDataFromComponents(
            List<ForageComponent> components,
            Map<String, ForageFactory> factoryMap,
            Map<String, List<ConfigEntry>> configsByPrefix,
            Map<String, List<BeanWithGav>> beansByComponent,
            Map<String, String> allConfigMappings) {

        for (ForageComponent component : components) {
            String artifactId = component.getArtifactId();
            String gav = FactoryVariant.createGav(component.getGroupId(), artifactId, component.getVersion());

            collectConfigEntries(component, artifactId, configsByPrefix);
            collectFactories(component, factoryMap, gav, allConfigMappings);
            collectBeans(component, beansByComponent, gav);
        }
    }

    /**
     * Collects configuration entries from a component.
     */
    private void collectConfigEntries(
            ForageComponent component, String artifactId, Map<String, List<ConfigEntry>> configsByPrefix) {
        if (component.getConfigurationProperties() != null
                && !component.getConfigurationProperties().isEmpty()) {
            configsByPrefix.put(artifactId, new ArrayList<>(component.getConfigurationProperties()));
        }
    }

    /**
     * Collects factories from a component.
     */
    private void collectFactories(
            ForageComponent component,
            Map<String, ForageFactory> factoryMap,
            String gav,
            Map<String, String> allConfigMappings) {

        if (component.getFactories() == null) {
            return;
        }

        for (ForageFactory scannedFactory : component.getFactories()) {
            String platform = mapVariantToPlatformKey(scannedFactory.getVariant());
            String factoryType = scannedFactory.getFactoryType();

            ForageFactory catalogFactory = factoryMap.computeIfAbsent(
                    factoryType, k -> createCatalogFactory(scannedFactory, allConfigMappings));

            // Add this platform variant
            FactoryVariant variant = new FactoryVariant(scannedFactory.getClassName(), gav);
            catalogFactory.getVariants().put(platform, variant);

            // Copy conditional beans if present
            if (scannedFactory.getConditionalBeans() != null
                    && !scannedFactory.getConditionalBeans().isEmpty()) {
                catalogFactory.setConditionalBeans(scannedFactory.getConditionalBeans());
            }
        }
    }

    /**
     * Creates a new ForageFactory for the catalog from a scanned factory.
     */
    private ForageFactory createCatalogFactory(ForageFactory scannedFactory, Map<String, String> allConfigMappings) {
        ForageFactory catalogFactory = new ForageFactory();
        catalogFactory.setName(scannedFactory.getName());
        catalogFactory.setFactoryType(scannedFactory.getFactoryType());
        catalogFactory.setDescription(scannedFactory.getDescription());
        catalogFactory.setComponents(scannedFactory.getComponents());
        catalogFactory.setAutowired(scannedFactory.isAutowired());
        catalogFactory.setVariants(new LinkedHashMap<>());
        catalogFactory.setBeansByFeature(new LinkedHashMap<>());
        catalogFactory.setPropertiesFile(determinePropertiesFile(scannedFactory, allConfigMappings));
        return catalogFactory;
    }

    /**
     * Collects beans from a component.
     */
    private void collectBeans(ForageComponent component, Map<String, List<BeanWithGav>> beansByComponent, String gav) {
        if (component.getBeans() == null) {
            return;
        }

        for (ForageBean bean : component.getBeans()) {
            BeanWithGav beanWithGav = new BeanWithGav(bean, gav, component.getConfigurationProperties());
            if (bean.getComponents() != null) {
                for (String comp : bean.getComponents()) {
                    beansByComponent
                            .computeIfAbsent(comp, k -> new ArrayList<>())
                            .add(beanWithGav);
                }
            }
        }
    }

    /**
     * Associates beans with their corresponding factories.
     */
    private void associateBeansWithFactories(
            Map<String, ForageFactory> factoryMap,
            Map<String, List<BeanWithGav>> beansByComponent,
            Map<String, String> allConfigMappings) {

        for (ForageFactory factory : factoryMap.values()) {
            if (factory.getComponents() == null) {
                continue;
            }

            // Track already-added beans by name to avoid duplicates
            Map<String, Set<String>> addedBeansByFeature = new HashMap<>();

            for (String component : factory.getComponents()) {
                List<BeanWithGav> matchingBeans = beansByComponent.get(component);
                if (matchingBeans != null) {
                    addBeansToFactory(factory, matchingBeans, addedBeansByFeature, allConfigMappings);
                }
            }
        }
    }

    /**
     * Adds beans to a factory, avoiding duplicates.
     */
    private void addBeansToFactory(
            ForageFactory factory,
            List<BeanWithGav> matchingBeans,
            Map<String, Set<String>> addedBeansByFeature,
            Map<String, String> allConfigMappings) {

        for (BeanWithGav beanWithGav : matchingBeans) {
            String feature = determineFeature(beanWithGav.bean, factory.getFactoryType());
            String beanName = beanWithGav.bean.getName();

            // Skip if already added
            Set<String> addedBeans = addedBeansByFeature.computeIfAbsent(feature, k -> new java.util.HashSet<>());
            if (addedBeans.contains(beanName)) {
                continue;
            }
            addedBeans.add(beanName);

            ForageBean catalogBean = createCatalogBean(beanWithGav, allConfigMappings);
            factory.getBeansByFeature()
                    .computeIfAbsent(feature, k -> new ArrayList<>())
                    .add(catalogBean);
        }
    }

    /**
     * Associates config entries with factories.
     */
    private void associateConfigEntriesWithFactories(
            Map<String, ForageFactory> factoryMap, Map<String, List<ConfigEntry>> configsByPrefix) {
        for (ForageFactory factory : factoryMap.values()) {
            List<ConfigEntry> factoryConfigs = findConfigsForFactory(factory, configsByPrefix);
            if (!factoryConfigs.isEmpty()) {
                factory.setConfigEntries(factoryConfigs);
            }
        }
    }

    /**
     * Map variant enum value to platform key used in catalog.
     */
    private String mapVariantToPlatformKey(String variant) {
        if (variant == null || variant.isEmpty()) {
            return org.apache.camel.forage.core.annotations.FactoryVariant.BASE.getKey();
        }

        try {
            return org.apache.camel.forage.core.annotations.FactoryVariant.valueOf(variant)
                    .getKey();
        } catch (IllegalArgumentException e) {
            log.warn("Unknown variant: " + variant + ", using BASE as fallback");
            return org.apache.camel.forage.core.annotations.FactoryVariant.BASE.getKey();
        }
    }

    /**
     * Determine the properties file name based on the factory's Config class.
     * Uses the Config class's name() method return value to determine the properties file name.
     */
    private String determinePropertiesFile(ForageFactory factory, Map<String, String> configMappings) {
        String configClassName = factory.getConfigClassName();

        // If no config class specified or it's the default Config.class, return null
        if (configClassName == null || configClassName.equals(Constants.DEFAULT_CONFIG_FQN)) {
            log.debug("No config class for factory: " + factory.getName());
            return null;
        }

        // Look up config name from mappings
        String configName = configMappings.get(configClassName);
        if (configName != null) {
            return configName + ".properties";
        }

        log.warn("Config class not found in mappings: " + configClassName + " for factory: " + factory.getName());
        return null;
    }

    /**
     * Determine the feature category for a bean based on its info and factory type.
     */
    private String determineFeature(ForageBean bean, String factoryType) {
        // Use the feature from annotation if available
        if (bean.getFeature() != null && !bean.getFeature().isEmpty()) {
            return bean.getFeature();
        }

        return org.apache.camel.forage.core.annotations.FactoryType.fromDisplayName(factoryType)
                .map(org.apache.camel.forage.core.annotations.FactoryType::toString)
                .orElse(factoryType);
    }

    /**
     * Create a ForageBean for the catalog output from the scanned bean with GAV and configs.
     */
    private ForageBean createCatalogBean(BeanWithGav beanWithGav, Map<String, String> configMappings) {
        ForageBean catalogBean = beanWithGav.bean.withGav(beanWithGav.gav);

        // Add config entries if available
        if (beanWithGav.configs != null && !beanWithGav.configs.isEmpty()) {
            catalogBean.setConfigEntries(new ArrayList<>(beanWithGav.configs));
        }

        // Set properties file based on bean's Config class
        catalogBean.setPropertiesFile(determineBeanPropertiesFile(beanWithGav.bean, configMappings));

        return catalogBean;
    }

    /**
     * Determine properties file name for a bean based on its Config class.
     * Uses the Config class's name() method return value to determine the properties file name.
     * Returns null for beans without individual configs (like JDBC beans).
     */
    private String determineBeanPropertiesFile(ForageBean bean, Map<String, String> configMappings) {
        String configClassName = bean.getConfigClassName();

        // Beans without configs (like JDBC beans) - no properties file
        if (configClassName == null || configClassName.equals(Constants.DEFAULT_CONFIG_FQN)) {
            return null;
        }

        // Look up config name from mappings
        String configName = configMappings.get(configClassName);
        if (configName != null) {
            return configName + ".properties";
        }

        log.debug("Config class not found in mappings: " + configClassName + " for bean: " + bean.getName());
        return null;
    }

    /**
     * Find config entries that belong to a factory.
     */
    private List<ConfigEntry> findConfigsForFactory(
            ForageFactory factory, Map<String, List<ConfigEntry>> configsByPrefix) {
        List<ConfigEntry> result = new ArrayList<>();

        // Match configs based on factory type
        String factoryType = factory.getFactoryType();
        if (factoryType == null || factoryType.isEmpty()) {
            return result;
        }

        // Look for common modules that match this factory type
        // The config artifact ID is now stored directly in the FactoryType enum
        String configPrefix = org.apache.camel.forage.core.annotations.FactoryType.fromDisplayName(factoryType)
                .map(org.apache.camel.forage.core.annotations.FactoryType::getConfigArtifactId)
                .orElse(null);

        if (configPrefix != null && configsByPrefix.containsKey(configPrefix)) {
            result.addAll(configsByPrefix.get(configPrefix));
        }

        return result;
    }

    private List<ForageComponent> discoverComponents(MavenProject project) {
        List<ForageComponent> components = new ArrayList<>();

        Set<Artifact> artifacts = project.getArtifacts();

        if (artifacts == null) {
            log.warn("No artifacts found in project");
            return components;
        }

        for (Artifact artifact : artifacts) {
            if (isForageComponent(artifact)) {
                log.debug("Processing Forage component: " + artifact.getArtifactId());
                ForageComponent component = createComponentFromArtifact(artifact, project.getParent());
                components.add(component);
            }
        }

        return components;
    }

    private boolean isForageComponent(Artifact artifact) {
        String groupId = artifact.getGroupId();
        return groupId.equals(Constants.FORAGE_GROUP_ID) || groupId.startsWith(Constants.FORAGE_GROUP_ID + ".");
    }

    private ForageComponent createComponentFromArtifact(Artifact artifact, MavenProject rootProject) {
        ForageComponent component = new ForageComponent();
        component.setArtifactId(artifact.getArtifactId());
        component.setGroupId(artifact.getGroupId());
        component.setVersion(artifact.getVersion());

        // Use single-pass scanning for better performance (Issue 1 & 2 optimizations)
        CodeScanner codeScanner = new CodeScanner(log);
        ScanResult scanResult = codeScanner.scanAllInOnePass(artifact, rootProject);

        component.setBeans(scanResult.getBeans());
        component.setFactories(scanResult.getFactories());
        component.setConfigurationProperties(scanResult.getConfigProperties());
        component.setConfigClasses(scanResult.getConfigClasses());

        return component;
    }

    private File generateJsonCatalog(ForageCatalog catalog, File outputDirectory) throws IOException {
        File outputFile = new File(outputDirectory, "forage-catalog.json");
        objectMapper.writeValue(outputFile, catalog);
        return outputFile;
    }

    private File generateYamlCatalog(ForageCatalog catalog, File outputDirectory) throws IOException {
        // For now, generate YAML as formatted JSON (would use YAML mapper in production)
        File outputFile = new File(outputDirectory, "forage-catalog.yaml");
        yamlObjectMapper.writeValue(outputFile, catalog);
        return outputFile;
    }

    // Setters for configuration
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Helper class to track bean with its GAV and configs.
     */
    private static class BeanWithGav {
        final ForageBean bean;
        final String gav;
        final List<ConfigEntry> configs;

        BeanWithGav(ForageBean bean, String gav, List<ConfigEntry> configs) {
            this.bean = bean;
            this.gav = gav;
            this.configs = configs;
        }
    }
}
