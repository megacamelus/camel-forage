package io.kaoto.forage.jms.common.transactions;

import com.arjuna.ats.arjuna.common.CoreEnvironmentBeanException;
import com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean;
import com.arjuna.ats.arjuna.common.RecoveryEnvironmentBean;
import com.arjuna.ats.internal.arjuna.objectstore.VolatileStore;
import io.kaoto.forage.jms.common.ConnectionFactoryConfig;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages Narayana transaction manager configuration for JMS operations.
 * Handles initialization of XA transaction support and recovery mechanisms.
 */
public class TransactionConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionConfiguration.class);

    private final ConnectionFactoryConfig config;
    private final String instanceId;

    public TransactionConfiguration(ConnectionFactoryConfig config, String instanceId) {
        this.config = config;
        this.instanceId = instanceId;
    }

    public void initializeNarayana() {
        LOG.info("Initializing Narayana transaction manager for instance: {}", instanceId);

        try {
            configureNodeIdentifier();
            configureObjectStore();
            configureRecovery();
            LOG.info("Narayana transaction manager initialized successfully");
        } catch (Exception e) {
            LOG.error("Failed to initialize Narayana transaction manager", e);
            throw new RuntimeException("Transaction manager initialization failed", e);
        }
    }

    private void configureNodeIdentifier() throws CoreEnvironmentBeanException {
        var coreEnvironmentBean = com.arjuna.ats.arjuna.common.arjPropertyManager.getCoreEnvironmentBean();

        String nodeId = config.transactionNodeId();
        if (nodeId != null) {
            LOG.debug("Setting transaction node identifier: {}", nodeId);
            coreEnvironmentBean.setNodeIdentifier(nodeId);
        } else {
            LOG.debug("Using default node identifier for instance: {}", instanceId);
        }
    }

    private void configureObjectStore() {
        ObjectStoreEnvironmentBean defaultActionStoreObjectStoreEnvironmentBean =
                com.arjuna.common.internal.util.propertyservice.BeanPopulator.getNamedInstance(
                        ObjectStoreEnvironmentBean.class, "default");

        String objectStoreType = config.transactionObjectStoreType();
        String objectStoreDir = config.transactionObjectStoreDirectory();

        LOG.debug("Configuring object store - Type: {}, Directory: {}", objectStoreType, objectStoreDir);

        if ("volatile".equalsIgnoreCase(objectStoreType)) {
            defaultActionStoreObjectStoreEnvironmentBean.setObjectStoreType(VolatileStore.class.getName());
        } else {
            defaultActionStoreObjectStoreEnvironmentBean.setObjectStoreDir(objectStoreDir);
        }
    }

    private void configureRecovery() {
        if (!config.transactionEnableRecovery()) {
            LOG.debug("Transaction recovery is disabled");
            return;
        }

        LOG.debug("Configuring transaction recovery mechanisms");

        RecoveryEnvironmentBean recoveryEnvironmentBean =
                com.arjuna.ats.arjuna.common.recoveryPropertyManager.getRecoveryEnvironmentBean();

        // Configure recovery modules
        String recoveryModules = config.transactionRecoveryModules();
        recoveryEnvironmentBean.setRecoveryModuleClassNames(Arrays.asList(recoveryModules.split(",")));

        // Configure expiry scanners
        String expiryScanners = config.transactionExpiryScanners();
        recoveryEnvironmentBean.setExpiryScannerClassNames(Arrays.asList(expiryScanners.split(",")));

        // Configure XA resource orphan filters
        String orphanFilters = config.transactionXaResourceOrphanFilters();
        var jtaEnvironmentBean = com.arjuna.ats.jta.common.jtaPropertyManager.getJTAEnvironmentBean();
        jtaEnvironmentBean.setXaResourceOrphanFilterClassNames(Arrays.asList(orphanFilters.split(",")));

        LOG.info("Transaction recovery configured with modules: {}", recoveryModules);
    }
}
