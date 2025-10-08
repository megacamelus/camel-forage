package org.apache.camel.forage.core.jta;

public final class RequiredJtaTransactionPolicy extends TransactionalJtaTransactionPolicy {

    @Override
    public void run(final Runnable runnable) throws Throwable {
        runWithTransaction(runnable, !hasActiveTransaction());
    }
}
