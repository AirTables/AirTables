package org.ingrahamrobotics.robottables;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TableCommunications {

    private final int[] listeningPorts;
    private final int sendingPort;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public TableCommunications(final int[] ports, final int port) {
        listeningPorts = ports;
        sendingPort = port;
    }

    private void send(String str) {
    }

    public void sendPublishRequest(String tableName) {

    }

    public void run(Runnable runnable) {
        executorService.execute(runnable);
    }
}
