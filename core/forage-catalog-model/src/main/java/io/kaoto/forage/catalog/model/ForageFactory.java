package io.kaoto.forage.catalog.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents a Forage factory in the catalog.
 * A factory contains platform variants, config entries, and beans grouped by feature.
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
    private FactoryVariants variants;

    @JsonProperty("configEntries")
    private List<ConfigEntry> configEntries;

    @JsonProperty("beansByFeature")
    private List<FeatureBeans> beansByFeature;

    @JsonProperty("conditionalBeans")
    private List<ConditionalBeanGroup> conditionalBeans;

    public ForageFactory() {}

    public ForageFactory(String name, String factoryType, String description) {
        this.name = name;
        this.factoryType = factoryType;
        this.description = description;
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

    public FactoryVariants getVariants() {
        return variants;
    }

    public void setVariants(FactoryVariants variants) {
        this.variants = variants;
    }

    public List<ConfigEntry> getConfigEntries() {
        return configEntries;
    }

    public void setConfigEntries(List<ConfigEntry> configEntries) {
        this.configEntries = configEntries;
    }

    public List<FeatureBeans> getBeansByFeature() {
        return beansByFeature;
    }

    public void setBeansByFeature(List<FeatureBeans> beansByFeature) {
        this.beansByFeature = beansByFeature;
    }

    public List<ConditionalBeanGroup> getConditionalBeans() {
        return conditionalBeans;
    }

    public void setConditionalBeans(List<ConditionalBeanGroup> conditionalBeans) {
        this.conditionalBeans = conditionalBeans;
    }

    @Override
    public String toString() {
        return "ForageFactory{" + "name='"
                + name + '\'' + ", factoryType='"
                + factoryType + '\'' + ", description='"
                + description + '\'' + ", components="
                + components + ", autowired="
                + autowired + '}';
    }
}
