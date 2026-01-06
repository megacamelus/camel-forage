package io.kaoto.forage.catalog.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Groups beans by a feature category.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeatureBeans {

    @JsonProperty("feature")
    private String feature;

    @JsonProperty("beans")
    private List<ForageBean> beans;

    public FeatureBeans() {}

    public FeatureBeans(String feature, List<ForageBean> beans) {
        this.feature = feature;
        this.beans = beans;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public List<ForageBean> getBeans() {
        return beans;
    }

    public void setBeans(List<ForageBean> beans) {
        this.beans = beans;
    }
}
