package org.apache.camel.forage.jdbc.jta;

import jakarta.transaction.Transaction;

public final class NotSupportedJtaTransactionPolicy extends TransactionalJtaTransactionPolicy {

    @Override
    public void run(final Runnable runnable) throws Throwable {
        Transaction suspendedTransaction = null;
        try {
            suspendedTransaction = suspendTransaction();
            runnable.run();
        } finally {
            resumeTransaction(suspendedTransaction);
        }
    }
}
