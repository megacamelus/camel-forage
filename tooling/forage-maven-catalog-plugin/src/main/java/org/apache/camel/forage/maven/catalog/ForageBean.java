package org.apache.camel.forage.maven.catalog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a Forage bean that can be used with a factory.
 * Beans are grouped by feature within a factory.
 *
 * This class is used both during scanning (to collect annotation data)
 * and in the output catalog (serialized to JSON/YAML).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForageBean {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("className")
    private String className;

    @JsonProperty("gav")
    private String gav;

    @JsonProperty("configEntries")
    private List<ConfigEntry> configEntries;

    @JsonProperty("propertiesFile")
    private String propertiesFile;

    // Fields used during scanning (not serialized to catalog output)
    @JsonIgnore
    private List<String> components;

    @JsonIgnore
    private String feature;

    @JsonIgnore
    private String configClassName;

    public ForageBean() {}

    public ForageBean(String name, String description, String className, String gav) {
        this.name = name;
        this.description = description;
        this.className = className;
        this.gav = gav;
    }

    /**
     * Constructor for scanning - captures all annotation data.
     */
    public ForageBean(String name, List<String> components, String description, String className, String feature) {
        this.name = name;
        this.components = components;
        this.description = description;
        this.className = className;
        this.feature = feature;
    }

    /**
     * Constructor for scanning with single component.
     */
    public ForageBean(String name, String component, String description, String className, String feature) {
        this.name = name;
        this.components = component != null && !component.isEmpty() ? Arrays.asList(component) : List.of();
        this.description = description;
        this.className = className;
        this.feature = feature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGav() {
        return gav;
    }

    public void setGav(String gav) {
        this.gav = gav;
    }

    public List<ConfigEntry> getConfigEntries() {
        return configEntries;
    }

    public void setConfigEntries(List<ConfigEntry> configEntries) {
        this.configEntries = configEntries;
    }

    public String getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getConfigClassName() {
        return configClassName;
    }

    public void setConfigClassName(String configClassName) {
        this.configClassName = configClassName;
    }

    /**
     * Creates a copy of this bean with GAV set for catalog output.
     * Copies relevant fields but excludes scanning-only fields from the result.
     */
    public ForageBean withGav(String gav) {
        ForageBean bean = new ForageBean();
        bean.setName(this.name);
        bean.setDescription(this.description);
        bean.setClassName(this.className);
        bean.setGav(gav);
        return bean;
    }

    @Override
    public String toString() {
        return "ForageBean{" + "name='"
                + name + '\'' + ", components="
                + components + ", description='"
                + description + '\'' + ", className='"
                + className + '\'' + ", feature='"
                + feature + '\'' + '}';
    }
}
