package org.apache.camel.forage.jdbc.jta;

public final class RequiredJtaTransactionPolicy extends TransactionalJtaTransactionPolicy {

    @Override
    public void run(final Runnable runnable) throws Throwable {
        runWithTransaction(runnable, !hasActiveTransaction());
    }
}
