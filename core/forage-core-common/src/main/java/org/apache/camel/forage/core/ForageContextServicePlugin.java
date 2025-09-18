package org.apache.camel.forage.core;

import java.util.ServiceLoader;
import org.apache.camel.CamelContext;
import org.apache.camel.forage.core.common.BeanFactory;
import org.apache.camel.spi.ContextServicePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForageContextServicePlugin implements ContextServicePlugin {
    private static final Logger LOG = LoggerFactory.getLogger(ForageContextServicePlugin.class);

    @Override
    public void load(CamelContext camelContext) {
        ServiceLoader<BeanFactory> loader =
                ServiceLoader.load(BeanFactory.class, camelContext.getApplicationContextClassLoader());

        loader.forEach(beanFactory -> {
            try {
                beanFactory.setCamelContext(camelContext);
                beanFactory.configure();
                LOG.debug(
                        "Successfully configured bean factory: {}",
                        beanFactory.getClass().getName());
            } catch (Exception e) {
                LOG.warn(
                        "Failed to configure bean factory: {}",
                        beanFactory.getClass().getName(),
                        e);
            }
        });
    }
}
