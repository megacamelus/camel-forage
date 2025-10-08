package org.apache.camel.forage.core.jta;

public final class SupportsJtaTransactionPolicy extends TransactionalJtaTransactionPolicy {

    @Override
    public void run(final Runnable runnable) throws Throwable {
        runnable.run();
    }
}
