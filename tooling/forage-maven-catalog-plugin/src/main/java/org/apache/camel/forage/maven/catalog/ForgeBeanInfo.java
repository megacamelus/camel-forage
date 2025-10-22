package org.apache.camel.forage.maven.catalog;

import java.util.Arrays;
import java.util.List;

/**
 * Represents information about a ForageBean annotation found in the codebase.
 */
public class ForgeBeanInfo {
    private String name;
    private List<String> components;
    private String description;
    private String className;
    private String feature;

    public ForgeBeanInfo() {}

    public ForgeBeanInfo(String name, List<String> components, String description, String className, String feature) {
        this.name = name;
        this.components = components;
        this.description = description;
        this.className = className;
        this.feature = feature;
    }

    public ForgeBeanInfo(String name, String component, String description, String className, String feature) {
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

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
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

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    @Override
    public String toString() {
        return "ForgeBeanInfo{" + "name='"
                + name + '\'' + ", components="
                + components + ", description='"
                + description + '\'' + ", className='"
                + className + '\'' + ", feature='"
                + feature + '\'' + '}';
    }
}
