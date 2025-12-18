package org.apache.camel.forage.maven.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds all scan results in one structure.
 */
public class ScanResult {
    private final List<ForageBean> beans = new ArrayList<>();
    private final List<ForageFactory> factories = new ArrayList<>();
    private final List<ConfigEntry> configProperties = new ArrayList<>();
    private final Map<String, String> configClasses = new HashMap<>();

    public List<ForageBean> getBeans() {
        return beans;
    }

    public List<ForageFactory> getFactories() {
        return factories;
    }

    public List<ConfigEntry> getConfigProperties() {
        return configProperties;
    }

    public Map<String, String> getConfigClasses() {
        return configClasses;
    }
}
