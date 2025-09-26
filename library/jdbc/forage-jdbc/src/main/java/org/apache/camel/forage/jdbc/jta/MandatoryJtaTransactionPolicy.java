package org.apache.camel.forage.jdbc.jta;

public final class MandatoryJtaTransactionPolicy extends TransactionalJtaTransactionPolicy {

    @Override
    public void run(final Runnable runnable) throws Throwable {
        if (!hasActiveTransaction()) {
            throw new IllegalStateException(
                    "Policy 'PROPAGATION_MANDATORY' is configured but no active transaction was found!");
        }
        runWithTransaction(runnable, false);
    }
}
