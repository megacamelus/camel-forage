package io.kaoto.forage.maven.catalog;

import io.kaoto.forage.catalog.model.ForageBean;
import java.util.Arrays;
import java.util.List;

/**
 * Internal wrapper class for beans discovered during source code scanning.
 * Contains both the serializable ForageBean model and scanning-specific metadata.
 */
public class ScannedBean {

    private final ForageBean bean;

    // Scanning-specific fields (not serialized to catalog)
    private List<String> components;
    private String feature;
    private String configClassName;

    public ScannedBean() {
        this.bean = new ForageBean();
    }

    public ScannedBean(String name, List<String> components, String description, String className, String feature) {
        this.bean = new ForageBean();
        this.bean.setName(name);
        this.bean.setDescription(description);
        this.bean.setClassName(className);
        this.components = components;
        this.feature = feature;
    }

    public ScannedBean(String name, String component, String description, String className, String feature) {
        this(
                name,
                component != null && !component.isEmpty() ? Arrays.asList(component) : List.of(),
                description,
                className,
                feature);
    }

    /**
     * Returns the underlying ForageBean model for serialization.
     */
    public ForageBean getBean() {
        return bean;
    }

    public String getName() {
        return bean.getName();
    }

    public void setName(String name) {
        bean.setName(name);
    }

    public String getDescription() {
        return bean.getDescription();
    }

    public void setDescription(String description) {
        bean.setDescription(description);
    }

    public String getClassName() {
        return bean.getClassName();
    }

    public void setClassName(String className) {
        bean.setClassName(className);
    }

    public String getGav() {
        return bean.getGav();
    }

    public void setGav(String gav) {
        bean.setGav(gav);
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
     * Creates a ForageBean for catalog output with GAV set.
     */
    public ForageBean toCatalogBean(String gav) {
        ForageBean catalogBean = new ForageBean();
        catalogBean.setName(bean.getName());
        catalogBean.setDescription(bean.getDescription());
        catalogBean.setClassName(bean.getClassName());
        catalogBean.setGav(gav);
        return catalogBean;
    }

    @Override
    public String toString() {
        return "ScannedBean{" + "name='"
                + getName() + '\'' + ", components="
                + components + ", description='"
                + getDescription() + '\'' + ", className='"
                + getClassName() + '\'' + ", feature='"
                + feature + '\'' + '}';
    }
}
