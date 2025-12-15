package org.apache.camel.forage.maven.catalog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Root catalog data structure containing all discovered Forage factories.
 * This is the simplified catalog structure designed for Kaoto integration.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForageCatalog {

    @JsonProperty("version")
    private String version;

    @JsonProperty("generatedBy")
    private String generatedBy;

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("factories")
    private List<ForageFactory> factories;

    public ForageCatalog() {}

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<ForageFactory> getFactories() {
        return factories;
    }

    public void setFactories(List<ForageFactory> factories) {
        this.factories = factories;
    }
}
