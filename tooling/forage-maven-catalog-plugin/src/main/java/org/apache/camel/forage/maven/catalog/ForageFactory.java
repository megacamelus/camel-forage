package org.apache.camel.forage.maven.catalog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Represents a Forage factory in the simplified catalog.
 * A factory contains platform variants, config entries, and beans grouped by feature.
 *
 * This class is used both during scanning (to collect annotation data)
 * and in the output catalog (serialized to JSON/YAML).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForageFactory {

    @JsonProperty("name")
    private String name;

    @JsonProperty("factoryType")
    private String factoryType;

    @JsonProperty("description")
    private String description;

    @JsonProperty("components")
    private List<String> components;

    @JsonProperty("autowired")
    private boolean autowired;

    @JsonProperty("propertiesFile")
    private String propertiesFile;

    @JsonProperty("variants")
    private Map<String, FactoryVariant> variants;

    @JsonProperty("configEntries")
    private List<ConfigEntry> configEntries;

    @JsonProperty("beansByFeature")
    private Map<String, List<ForageBean>> beansByFeature;

    @JsonProperty("conditionalBeans")
    private List<ConditionalBeanGroup> conditionalBeans;

    // Fields used during scanning (not serialized to catalog output)
    @JsonIgnore
    private String className;

    @JsonIgnore
    private String variant;

    @JsonIgnore
    private String configClassName;

    public ForageFactory() {}

    public ForageFactory(String name, String factoryType, String description) {
        this.name = name;
        this.factoryType = factoryType;
        this.description = description;
    }

    /**
     * Constructor for scanning - captures all annotation data.
     */
    public ForageFactory(
            String name,
            List<String> components,
            String description,
            String factoryType,
            String className,
            boolean autowired) {
        this.name = name;
        this.components = components;
        this.description = description;
        this.factoryType = factoryType;
        this.className = className;
        this.autowired = autowired;
    }

    /**
     * Constructor for scanning with single component.
     */
    public ForageFactory(String name, String component, String description, String factoryType, String className) {
        this.name = name;
        this.components = component != null && !component.isEmpty() ? Arrays.asList(component) : List.of();
        this.description = description;
        this.factoryType = factoryType;
        this.className = className;
        this.autowired = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFactoryType() {
        return factoryType;
    }

    public void setFactoryType(String factoryType) {
        this.factoryType = factoryType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    public boolean isAutowired() {
        return autowired;
    }

    public void setAutowired(boolean autowired) {
        this.autowired = autowired;
    }

    public String getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public Map<String, FactoryVariant> getVariants() {
        return variants;
    }

    public void setVariants(Map<String, FactoryVariant> variants) {
        this.variants = variants;
    }

    public List<ConfigEntry> getConfigEntries() {
        return configEntries;
    }

    public void setConfigEntries(List<ConfigEntry> configEntries) {
        this.configEntries = configEntries;
    }

    public Map<String, List<ForageBean>> getBeansByFeature() {
        return beansByFeature;
    }

    public void setBeansByFeature(Map<String, List<ForageBean>> beansByFeature) {
        this.beansByFeature = beansByFeature;
    }

    public List<ConditionalBeanGroup> getConditionalBeans() {
        return conditionalBeans;
    }

    public void setConditionalBeans(List<ConditionalBeanGroup> conditionalBeans) {
        this.conditionalBeans = conditionalBeans;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getConfigClassName() {
        return configClassName;
    }

    public void setConfigClassName(String configClassName) {
        this.configClassName = configClassName;
    }

    @Override
    public String toString() {
        return "ForageFactory{" + "name='"
                + name + '\'' + ", components="
                + components + ", description='"
                + description + '\'' + ", factoryType='"
                + factoryType + '\'' + ", className='"
                + className + '\'' + ", autowired="
                + autowired + ", variant='"
                + variant + '\'' + '}';
    }
}
