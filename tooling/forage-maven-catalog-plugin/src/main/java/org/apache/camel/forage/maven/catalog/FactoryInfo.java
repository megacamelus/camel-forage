package org.apache.camel.forage.maven.catalog;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Arrays;
import java.util.List;

/**
 * Represents information about a ForageFactory annotation found in the codebase.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FactoryInfo {
    private String name;
    private List<String> components;
    private String description;
    private String factoryType;
    private String className;
    private boolean autowired;
    private List<ConditionalBeanGroup> conditionalBeans;

    public FactoryInfo() {}

    public FactoryInfo(
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

    public FactoryInfo(String name, String component, String description, String factoryType, String className) {
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

    public String getFactoryType() {
        return factoryType;
    }

    public void setFactoryType(String factoryType) {
        this.factoryType = factoryType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isAutowired() {
        return autowired;
    }

    public void setAutowired(boolean autowired) {
        this.autowired = autowired;
    }

    public List<ConditionalBeanGroup> getConditionalBeans() {
        return conditionalBeans;
    }

    public void setConditionalBeans(List<ConditionalBeanGroup> conditionalBeans) {
        this.conditionalBeans = conditionalBeans;
    }

    @Override
    public String toString() {
        return "FactoryInfo{" + "name='"
                + name + '\'' + ", components="
                + components + ", description='"
                + description + '\'' + ", factoryType='"
                + factoryType + '\'' + ", className='"
                + className + '\'' + ", autowired="
                + autowired + '}';
    }
}
