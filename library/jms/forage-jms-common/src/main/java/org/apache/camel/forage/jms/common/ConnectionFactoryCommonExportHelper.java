package org.apache.camel.forage.jms.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for transforming JMS kind identifiers into provider class names.
 */
public class ConnectionFactoryCommonExportHelper {
    private static final Map<String, String> JMS_KIND_TO_PROVIDER_CLASS = new HashMap<>();

    static {
        JMS_KIND_TO_PROVIDER_CLASS.put("artemis", "org.apache.camel.forage.jms.artemis.ArtemisJms");
        JMS_KIND_TO_PROVIDER_CLASS.put("ibmmq", "org.apache.camel.forage.jms.ibmmq.IbmMqJms");
    }

    public static String transformJmsKindIntoProviderClass(String jmsKind) {
        return JMS_KIND_TO_PROVIDER_CLASS.getOrDefault(
                jmsKind.toLowerCase(), "org.apache.camel.forage.jms.artemis.ArtemisJms");
    }
}
