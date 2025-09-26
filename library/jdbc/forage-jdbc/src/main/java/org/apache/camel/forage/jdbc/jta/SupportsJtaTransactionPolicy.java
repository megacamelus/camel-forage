package org.apache.camel.forage.jdbc.jta;

public final class SupportsJtaTransactionPolicy extends TransactionalJtaTransactionPolicy {

    @Override
    public void run(final Runnable runnable) throws Throwable {
        runnable.run();
    }
}
