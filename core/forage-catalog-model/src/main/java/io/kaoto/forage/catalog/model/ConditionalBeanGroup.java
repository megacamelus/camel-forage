package io.kaoto.forage.catalog.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents a group of beans that are conditionally registered when a boolean config entry is enabled.
 *
 * This class is used in the catalog JSON to inform UI wizards about beans that are automatically
 * created when certain configuration options are enabled.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConditionalBeanGroup {

    @JsonProperty("id")
    private String id;

    @JsonProperty("description")
    private String description;

    @JsonProperty("configEntry")
    private String configEntry;

    @JsonProperty("beans")
    private List<ConditionalBeanInfo> beans;

    public ConditionalBeanGroup() {}

    public ConditionalBeanGroup(String id, String description, String configEntry, List<ConditionalBeanInfo> beans) {
        this.id = id;
        this.description = description;
        this.configEntry = configEntry;
        this.beans = beans;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConfigEntry() {
        return configEntry;
    }

    public void setConfigEntry(String configEntry) {
        this.configEntry = configEntry;
    }

    public List<ConditionalBeanInfo> getBeans() {
        return beans;
    }

    public void setBeans(List<ConditionalBeanInfo> beans) {
        this.beans = beans;
    }

    @Override
    public String toString() {
        return "ConditionalBeanGroup{" + "id='"
                + id + '\'' + ", description='"
                + description + '\'' + ", configEntry='"
                + configEntry + '\'' + ", beans="
                + beans + '}';
    }
}
