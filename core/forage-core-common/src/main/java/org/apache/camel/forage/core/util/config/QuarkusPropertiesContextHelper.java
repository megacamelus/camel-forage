package org.apache.camel.forage.core.util.config;

import io.smallrye.config.SmallRyeConfig;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class QuarkusPropertiesContextHelper {
    public static SmallRyeConfig getConfig() {
        Config config = ConfigProvider.getConfig();
        if (config != null) {
            return (SmallRyeConfig) config.unwrap(SmallRyeConfig.class);
        } else {
            return null;
        }
    }
}
