package org.apache.camel.forage.maven.catalog;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents information about a bean that is conditionally registered.
 *
 * Bean names can be either fixed or dynamic:
 * <ul>
 *   <li>Fixed: {@link #name} contains the exact bean name</li>
 *   <li>Dynamic: {@link #nameFromConfig} references a config entry that provides the name</li>
 * </ul>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConditionalBeanInfo {
    private String name;
    private String nameFromConfig;
    private String javaType;
    private String description;

    public ConditionalBeanInfo() {}

    public ConditionalBeanInfo(String name, String nameFromConfig, String javaType, String description) {
        this.name = name;
        this.nameFromConfig = nameFromConfig;
        this.javaType = javaType;
        this.description = description;
    }

    /**
     * Creates a ConditionalBeanInfo with a fixed name.
     */
    public static ConditionalBeanInfo withName(String name, String javaType, String description) {
        return new ConditionalBeanInfo(name, null, javaType, description);
    }

    /**
     * Creates a ConditionalBeanInfo with a dynamic name from config.
     */
    public static ConditionalBeanInfo withNameFromConfig(String nameFromConfig, String javaType, String description) {
        return new ConditionalBeanInfo(null, nameFromConfig, javaType, description);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameFromConfig() {
        return nameFromConfig;
    }

    public void setNameFromConfig(String nameFromConfig) {
        this.nameFromConfig = nameFromConfig;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "ConditionalBeanInfo{" + "name='"
                + name + '\'' + ", nameFromConfig='"
                + nameFromConfig + '\'' + ", javaType='"
                + javaType + '\'' + ", description='"
                + description + '\'' + '}';
    }
}
