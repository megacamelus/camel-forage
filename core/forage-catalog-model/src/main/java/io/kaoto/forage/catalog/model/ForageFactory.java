/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.kaoto.forage.catalog.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents a Forage factory in the catalog.
 * A factory contains platform variants, config entries, and beans grouped by feature.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForageFactory {

    @JsonProperty("name")
    private String name;

    @JsonProperty("factoryType")
    private String factoryType;

    @JsonProperty("description")
    private String description;

    @JsonProperty("components")
    private List<String> components;

    @JsonProperty("autowired")
    private boolean autowired;

    @JsonProperty("propertiesFile")
    private String propertiesFile;

    @JsonProperty("variants")
    private FactoryVariants variants;

    @JsonProperty("configEntries")
    private List<ConfigEntry> configEntries;

    @JsonProperty("beansByFeature")
    private List<FeatureBeans> beansByFeature;

    @JsonProperty("conditionalBeans")
    private List<ConditionalBeanGroup> conditionalBeans;

    public ForageFactory() {}

    public ForageFactory(String name, String factoryType, String description) {
        this.name = name;
        this.factoryType = factoryType;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFactoryType() {
        return factoryType;
    }

    public void setFactoryType(String factoryType) {
        this.factoryType = factoryType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    public boolean isAutowired() {
        return autowired;
    }

    public void setAutowired(boolean autowired) {
        this.autowired = autowired;
    }

    public String getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public FactoryVariants getVariants() {
        return variants;
    }

    public void setVariants(FactoryVariants variants) {
        this.variants = variants;
    }

    public List<ConfigEntry> getConfigEntries() {
        return configEntries;
    }

    public void setConfigEntries(List<ConfigEntry> configEntries) {
        this.configEntries = configEntries;
    }

    public List<FeatureBeans> getBeansByFeature() {
        return beansByFeature;
    }

    public void setBeansByFeature(List<FeatureBeans> beansByFeature) {
        this.beansByFeature = beansByFeature;
    }

    public List<ConditionalBeanGroup> getConditionalBeans() {
        return conditionalBeans;
    }

    public void setConditionalBeans(List<ConditionalBeanGroup> conditionalBeans) {
        this.conditionalBeans = conditionalBeans;
    }

    @Override
    public String toString() {
        return "ForageFactory{" + "name='"
                + name + '\'' + ", factoryType='"
                + factoryType + '\'' + ", description='"
                + description + '\'' + ", components="
                + components + ", autowired="
                + autowired + '}';
    }
}
