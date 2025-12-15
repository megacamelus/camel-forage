package org.apache.camel.forage.maven.catalog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents a Forage bean that can be used with a factory.
 * Beans are grouped by feature within a factory.
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

    public ForageBean() {}

    public ForageBean(String name, String description, String className, String gav) {
        this.name = name;
        this.description = description;
        this.className = className;
        this.gav = gav;
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

    /**
     * Creates a ForageBean from a ForgeBeanInfo (for migration).
     */
    public static ForageBean from(ForgeBeanInfo info, String gav) {
        ForageBean bean = new ForageBean();
        bean.setName(info.getName());
        bean.setDescription(info.getDescription());
        bean.setClassName(info.getClassName());
        bean.setGav(gav);
        return bean;
    }

    @Override
    public String toString() {
        return "ForageBean{" + "name='"
                + name + '\'' + ", description='"
                + description + '\'' + ", className='"
                + className + '\'' + ", gav='"
                + gav + '\'' + '}';
    }
}
