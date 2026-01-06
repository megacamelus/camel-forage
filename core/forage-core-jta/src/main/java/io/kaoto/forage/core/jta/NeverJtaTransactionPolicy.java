package io.kaoto.forage.core.jta;

public final class NeverJtaTransactionPolicy extends TransactionalJtaTransactionPolicy {

    @Override
    public void run(final Runnable runnable) throws Throwable {
        if (hasActiveTransaction()) {
            throw new IllegalStateException(
                    "Policy 'PROPAGATION_NEVER' is configured but an active transaction was found!");
        }

        runnable.run();
    }
}
