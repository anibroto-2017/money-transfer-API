package com.revolut.moneytransfer;

import org.junit.rules.ExternalResource;

public class MoneyTransferTestResource extends ExternalResource {
    @Override
    protected void before() throws Exception {
        MoneyTransferApplication.initServer();
        MoneyTransferApplication.server.start();
    }

    @Override
    protected void after() {
        if (MoneyTransferApplication.server.isRunning()) {
            try {
                MoneyTransferApplication.server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
