package org.apache.camel.forage.maven.catalog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a configuration entry for a Forage factory or bean.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigEntry {

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("description")
    private String description;

    @JsonProperty("required")
    private boolean required;

    @JsonProperty("defaultValue")
    private String defaultValue;

    @JsonProperty("example")
    private String example;

    @JsonProperty("label")
    private String label;

    @JsonProperty("configTag")
    private String configTag;

    public ConfigEntry() {}

    public ConfigEntry(String name, String type, String description, boolean required) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getConfigTag() {
        return configTag;
    }

    public void setConfigTag(String configTag) {
        this.configTag = configTag;
    }

    /**
     * Creates a ConfigEntry from a ConfigurationProperty (for migration).
     */
    public static ConfigEntry from(ConfigurationProperty prop) {
        ConfigEntry entry = new ConfigEntry();
        entry.setName(prop.getName());
        entry.setType(prop.getType());
        entry.setDescription(prop.getDescription());
        entry.setRequired(prop.isRequired());
        entry.setDefaultValue(prop.getDefaultValue());
        entry.setExample(prop.getExample());
        entry.setLabel(prop.getLabel());
        entry.setConfigTag(prop.getConfigTag());
        return entry;
    }
}
