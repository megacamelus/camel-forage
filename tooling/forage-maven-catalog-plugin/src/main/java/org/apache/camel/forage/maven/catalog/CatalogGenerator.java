package org.apache.camel.forage.maven.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

    private String format = "json";

    public CatalogGenerator(Log log) {
        this.log = log;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
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
        // Map to collect factories by name (to merge platform variants)
        Map<String, ForageFactory> factoryMap = new LinkedHashMap<>();

        // Map to collect config entries by artifact prefix (e.g., "forage-jdbc-common" -> jdbc configs)
        Map<String, List<ConfigEntry>> configsByPrefix = new HashMap<>();

        // Map to collect beans by components they support
        Map<String, List<BeanWithGav>> beansByComponent = new HashMap<>();

        // First pass: collect all data from components
        for (ForageComponent component : components) {
            String artifactId = component.getArtifactId();
            String gav = FactoryVariant.createGav(component.getGroupId(), artifactId, component.getVersion());

            // Collect config entries from common modules
            if (component.getConfigurationProperties() != null
                    && !component.getConfigurationProperties().isEmpty()) {
                List<ConfigEntry> entries = component.getConfigurationProperties().stream()
                        .map(ConfigEntry::from)
                        .collect(Collectors.toList());
                configsByPrefix.put(artifactId, entries);
            }

            // Collect factories
            if (component.getFactories() != null) {
                for (FactoryInfo factoryInfo : component.getFactories()) {
                    String platform = detectPlatform(artifactId);
                    String factoryName = factoryInfo.getName();

                    ForageFactory factory = factoryMap.computeIfAbsent(factoryName, k -> {
                        ForageFactory f = new ForageFactory();
                        f.setName(factoryName);
                        f.setFactoryType(factoryInfo.getFactoryType());
                        f.setDescription(factoryInfo.getDescription());
                        f.setComponents(factoryInfo.getComponents());
                        f.setAutowired(factoryInfo.isAutowired());
                        f.setVariants(new LinkedHashMap<>());
                        f.setBeansByFeature(new LinkedHashMap<>());
                        f.setPropertiesFile(determinePropertiesFile(factoryInfo.getFactoryType()));
                        return f;
                    });

                    // Add this platform variant
                    FactoryVariant variant = new FactoryVariant(factoryInfo.getClassName(), gav);
                    factory.getVariants().put(platform, variant);

                    // Copy conditional beans if present
                    if (factoryInfo.getConditionalBeans() != null
                            && !factoryInfo.getConditionalBeans().isEmpty()) {
                        factory.setConditionalBeans(factoryInfo.getConditionalBeans());
                    }
                }
            }

            // Collect beans with their components
            if (component.getBeans() != null) {
                for (ForgeBeanInfo beanInfo : component.getBeans()) {
                    BeanWithGav beanWithGav = new BeanWithGav(beanInfo, gav, component.getConfigurationProperties());
                    for (String comp : beanInfo.getComponents()) {
                        beansByComponent
                                .computeIfAbsent(comp, k -> new ArrayList<>())
                                .add(beanWithGav);
                    }
                }
            }
        }

        // Second pass: associate beans with factories and link configs
        for (ForageFactory factory : factoryMap.values()) {
            // Track already-added beans by name to avoid duplicates
            Map<String, Set<String>> addedBeansByFeature = new HashMap<>();

            // Find beans that match this factory's components
            if (factory.getComponents() != null) {
                for (String component : factory.getComponents()) {
                    List<BeanWithGav> matchingBeans = beansByComponent.get(component);
                    if (matchingBeans != null) {
                        for (BeanWithGav beanWithGav : matchingBeans) {
                            String feature = determineFeature(beanWithGav.info, factory.getFactoryType());
                            String beanName = beanWithGav.info.getName();

                            // Skip if already added
                            Set<String> addedBeans =
                                    addedBeansByFeature.computeIfAbsent(feature, k -> new java.util.HashSet<>());
                            if (addedBeans.contains(beanName)) {
                                continue;
                            }
                            addedBeans.add(beanName);

                            ForageBean bean = createForageBean(beanWithGav);
                            factory.getBeansByFeature()
                                    .computeIfAbsent(feature, k -> new ArrayList<>())
                                    .add(bean);
                        }
                    }
                }
            }

            // Find and associate config entries
            List<ConfigEntry> factoryConfigs = findConfigsForFactory(factory, configsByPrefix);
            if (!factoryConfigs.isEmpty()) {
                factory.setConfigEntries(factoryConfigs);
            }
        }

        return new ArrayList<>(factoryMap.values());
    }

    /**
     * Detect platform type from artifact ID.
     */
    private String detectPlatform(String artifactId) {
        if (artifactId.contains("-starter") || artifactId.contains("-spring-boot")) {
            return "springboot";
        } else if (artifactId.contains("-quarkus")) {
            return "quarkus";
        }
        return "base";
    }

    /**
     * Determine the properties file name based on factory type.
     */
    private String determinePropertiesFile(String factoryType) {
        if (factoryType == null) {
            return null;
        }
        switch (factoryType) {
            case "DataSource":
                return "forage-datasource-factory.properties";
            case "ConnectionFactory":
                return "forage-connectionfactory.properties";
            case "Agent":
                return "forage-agent-factory.properties";
            default:
                return "forage-" + factoryType.toLowerCase() + "-factory.properties";
        }
    }

    /**
     * Determine the feature category for a bean based on its info and factory type.
     */
    private String determineFeature(ForgeBeanInfo beanInfo, String factoryType) {
        // Use the feature from annotation if available
        if (beanInfo.getFeature() != null && !beanInfo.getFeature().isEmpty()) {
            return beanInfo.getFeature();
        }

        // Default feature based on factory type
        if ("DataSource".equals(factoryType)) {
            return "Database";
        } else if ("ConnectionFactory".equals(factoryType)) {
            return "JMS Broker";
        } else if ("Agent".equals(factoryType)) {
            return "Chat Model";
        }
        return "Default";
    }

    /**
     * Create a ForageBean from bean info with GAV and configs.
     */
    private ForageBean createForageBean(BeanWithGav beanWithGav) {
        ForageBean bean = ForageBean.from(beanWithGav.info, beanWithGav.gav);

        // Add config entries if available
        if (beanWithGav.configs != null && !beanWithGav.configs.isEmpty()) {
            List<ConfigEntry> beanConfigs =
                    beanWithGav.configs.stream().map(ConfigEntry::from).collect(Collectors.toList());
            bean.setConfigEntries(beanConfigs);
            bean.setPropertiesFile(determineBeanPropertiesFile(beanWithGav.info.getName()));
        }

        return bean;
    }

    /**
     * Determine properties file name for a bean.
     */
    private String determineBeanPropertiesFile(String beanName) {
        return "forage-" + beanName.replace("-", "-") + ".properties";
    }

    /**
     * Find config entries that belong to a factory.
     */
    private List<ConfigEntry> findConfigsForFactory(
            ForageFactory factory, Map<String, List<ConfigEntry>> configsByPrefix) {
        List<ConfigEntry> result = new ArrayList<>();

        // Match configs based on factory type
        String factoryType = factory.getFactoryType();
        if (factoryType == null) {
            return result;
        }

        // Look for common modules that match this factory type
        String configPrefix =
                switch (factoryType) {
                    case "DataSource" -> "forage-jdbc-common";
                    case "ConnectionFactory" -> "forage-jms-common";
                    case "Agent" -> "forage-agent-factories";
                    default -> null;
                };

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
        return artifact.getGroupId().startsWith("org.apache.camel.forage")
                && artifact.getArtifactId().startsWith("forage-");
    }

    private ForageComponent createComponentFromArtifact(Artifact artifact, MavenProject rootProject) {
        ForageComponent component = new ForageComponent();
        component.setArtifactId(artifact.getArtifactId());
        component.setGroupId(artifact.getGroupId());
        component.setVersion(artifact.getVersion());

        // Scan for ForageBean annotations
        CodeScanner codeScanner = new CodeScanner(log);

        List<ForgeBeanInfo> beans = codeScanner.scanForForageBeans(artifact, rootProject);
        component.setBeans(beans);

        // Scan for ForageFactory annotations (conditionalBeans are now extracted directly from @ForageFactory)
        List<FactoryInfo> factories = codeScanner.scanForForageFactories(artifact, rootProject);
        component.setFactories(factories);

        // Scan for configuration properties from ConfigEntries classes
        List<ConfigurationProperty> configProperties =
                codeScanner.scanForConfigurationProperties(artifact, rootProject);
        component.setConfigurationProperties(configProperties);

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
        objectMapper.writeValue(outputFile, catalog);
        return outputFile;
    }

    // Setters for configuration
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Helper class to track bean info with its GAV and configs.
     */
    private static class BeanWithGav {
        final ForgeBeanInfo info;
        final String gav;
        final List<ConfigurationProperty> configs;

        BeanWithGav(ForgeBeanInfo info, String gav, List<ConfigurationProperty> configs) {
            this.info = info;
            this.gav = gav;
            this.configs = configs;
        }
    }
}
