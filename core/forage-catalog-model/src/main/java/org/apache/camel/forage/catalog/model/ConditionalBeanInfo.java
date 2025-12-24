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
package org.apache.camel.forage.catalog.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty("name")
    private String name;

    @JsonProperty("nameFromConfig")
    private String nameFromConfig;

    @JsonProperty("javaType")
    private String javaType;

    @JsonProperty("description")
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
