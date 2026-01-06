package io.kaoto.forage.jdbc.common.transactions;

import com.arjuna.ats.arjuna.common.CoordinatorEnvironmentBean;
import com.arjuna.ats.arjuna.common.CoreEnvironmentBean;
import com.arjuna.ats.arjuna.common.CoreEnvironmentBeanException;
import com.arjuna.ats.arjuna.common.ObjectStoreEnvironmentBean;
import com.arjuna.ats.arjuna.common.RecoveryEnvironmentBean;
import com.arjuna.ats.jta.common.JTAEnvironmentBean;
import com.arjuna.common.internal.util.propertyservice.BeanPopulator;
import io.agroal.narayana.LocalXAResource;
import io.kaoto.forage.jdbc.common.DataSourceFactoryConfig;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionConfiguration {

    private static final Logger log = LoggerFactory.getLogger(TransactionConfiguration.class);

    private final DataSourceFactoryConfig config;
    private String transactionNodeId;

    public TransactionConfiguration(DataSourceFactoryConfig config, String id) {
        this.config = config;
        if (config.transactionNodeId() == null) {
            transactionNodeId = id;
        } else {
            transactionNodeId = config.transactionNodeId();
        }
        log.info("TransactionConfiguration initialized with nodeId: {}", transactionNodeId);
    }

    public void initializeNarayana() {
        log.info("Initializing Narayana transaction manager with nodeId: {}", transactionNodeId);
        try {
            configureCoreEnvironment();
        } catch (CoreEnvironmentBeanException e) {
            log.error("Failed to configure core environment for transaction manager", e);
            throw new RuntimeException(e);
        }
        configureObjectStore();
        configureCoordinator();
        configureRecovery();
        configureJTA();
        log.info("Narayana transaction manager initialization completed");
    }

    private void configureCoreEnvironment() throws CoreEnvironmentBeanException {
        log.debug("Configuring core environment with nodeId: {}", transactionNodeId);
        CoreEnvironmentBean coreBean = BeanPopulator.getDefaultInstance(CoreEnvironmentBean.class);
        coreBean.setNodeIdentifier(transactionNodeId);
        log.debug("Core environment configured successfully");
    }

    private void configureObjectStore() {
        log.debug(
                "Configuring object store with type: {} and directory: {}",
                config.transactionObjectStoreType(),
                config.transactionObjectStoreDirectory());
        ObjectStoreEnvironmentBean osBean = BeanPopulator.getDefaultInstance(ObjectStoreEnvironmentBean.class);
        if ("file-system".equals(config.transactionObjectStoreType())) {
            osBean.setObjectStoreDir(config.transactionObjectStoreDirectory());
        }

        // TODO Handle jdbc case, a datasource has to be created for this use case
        //        if ("jdbc".equals(config.transactionObjectStoreType())) {
        //            osBean.setObjectStoreType(JDBCStore.class.getName());
        //            osBean.setJdbcDataSource(objectStorageDataSource);
        //
        //            osBean.setCreateTable(config.transactionObjectStoreCreateTable());
        //            osBean.setDropTable(config.transactionObjectStoreDropTable());
        //            osBean.setTablePrefix(config.transactionObjectStoreTablePrefix());
        //        }
        log.debug("Object store configured successfully");
    }

    private void configureCoordinator() {
        log.debug("Configuring coordinator with timeout: {} seconds", config.transactionTimeoutSeconds());
        BeanPopulator.getDefaultInstance(CoordinatorEnvironmentBean.class)
                .setDefaultTimeout(config.transactionTimeoutSeconds());
        log.debug("Coordinator configured successfully");
    }

    private void configureRecovery() {
        if (config.transactionEnableRecovery()) {
            log.debug(
                    "Configuring recovery with modules: {} and expiry scanners: {}",
                    config.transactionRecoveryModules(),
                    config.transactionExpiryScanners());
            BeanPopulator.getDefaultInstance(RecoveryEnvironmentBean.class)
                    .setRecoveryModuleClassNames(
                            Arrays.stream(config.transactionRecoveryModules().split(","))
                                    .map(String::trim)
                                    .toList());
            BeanPopulator.getDefaultInstance(RecoveryEnvironmentBean.class)
                    .setExpiryScannerClassNames(
                            Arrays.stream(config.transactionExpiryScanners().split(","))
                                    .map(String::trim)
                                    .toList());
            log.debug("Recovery configured successfully");
        } else {
            log.debug("Recovery disabled in configuration");
        }
    }

    private void configureJTA() {
        log.debug(
                "Configuring JTA with recovery nodes: {} and orphan filters: {}",
                transactionNodeId,
                config.transactionXaResourceOrphanFilters());
        BeanPopulator.getDefaultInstance(JTAEnvironmentBean.class).setXaRecoveryNodes(List.of(transactionNodeId));
        BeanPopulator.getDefaultInstance(JTAEnvironmentBean.class)
                .setLastResourceOptimisationInterface(LocalXAResource.class);
        BeanPopulator.getDefaultInstance(JTAEnvironmentBean.class)
                .setXaResourceOrphanFilterClassNames(Arrays.stream(
                                config.transactionXaResourceOrphanFilters().split(","))
                        .map(String::trim)
                        .toList());
        log.debug("JTA configured successfully");
    }
}
