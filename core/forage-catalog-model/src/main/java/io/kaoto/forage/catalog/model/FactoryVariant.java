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

/**
 * Represents a platform-specific variant of a Forage factory.
 * Each factory can have multiple variants for different platforms (base, springboot, quarkus).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FactoryVariant {

    @JsonProperty("className")
    private String className;

    @JsonProperty("gav")
    private String gav;

    public FactoryVariant() {}

    public FactoryVariant(String className, String gav) {
        this.className = className;
        this.gav = gav;
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

    /**
     * Creates a GAV string from individual components.
     */
    public static String createGav(String groupId, String artifactId, String version) {
        return groupId + ":" + artifactId + ":" + version;
    }

    @Override
    public String toString() {
        return "FactoryVariant{" + "className='" + className + '\'' + ", gav='" + gav + '\'' + '}';
    }
}
