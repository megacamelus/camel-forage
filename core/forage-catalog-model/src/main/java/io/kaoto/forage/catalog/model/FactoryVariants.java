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
 * Contains factory variants for different runtime platforms.
 * Each property represents a specific platform variant.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FactoryVariants {

    @JsonProperty("base")
    private FactoryVariant base;

    @JsonProperty("springboot")
    private FactoryVariant springboot;

    @JsonProperty("quarkus")
    private FactoryVariant quarkus;

    public FactoryVariants() {}

    public FactoryVariant getBase() {
        return base;
    }

    public void setBase(FactoryVariant base) {
        this.base = base;
    }

    public FactoryVariant getSpringboot() {
        return springboot;
    }

    public void setSpringboot(FactoryVariant springboot) {
        this.springboot = springboot;
    }

    public FactoryVariant getQuarkus() {
        return quarkus;
    }

    public void setQuarkus(FactoryVariant quarkus) {
        this.quarkus = quarkus;
    }
}
