package net.daboross.airtables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.daboross.airtables.api.AirTable;
import net.daboross.airtables.api.AirTablesClient;
import net.daboross.airtables.api.Callback;
import net.daboross.airtables.api.TableType;
import net.daboross.airtables.api.listeners.ClientUpdateListener;

public class AirTables implements AirTablesClient {

    private ExecutorService eventService = Executors.newSingleThreadExecutor();
    private Map<String, InternalAirTable> airTableMap = new HashMap<String, InternalAirTable>();
    private List<ClientUpdateListener> listeners = new ArrayList<ClientUpdateListener>();
    private final TableCommunications communications;

    public AirTables(final int[] ports, final int port) {
        communications = new TableCommunications(ports, port);
    }

    InternalAirTable externalPublishedTable(final String tableName) {
        InternalAirTable airTable = airTableMap.get(tableName);
        if (airTable != null) {
            airTable.clear(); // TODO: Should we clear all values when a table is re-published?
            if (airTable.getType() != TableType.REMOTE) {
                TableType oldType = airTable.getType();
                airTable.setType(TableType.REMOTE);
                fireTableTypeChangeEvent(airTable, oldType, TableType.REMOTE);
            }
        } else {
            airTable = new InternalAirTable(TableType.REMOTE);
            airTableMap.put(tableName, airTable);
            fireNewTableEvent(airTable);
        }
        return airTable;
    }

    void fireTableTypeChangeEvent(final AirTable table, final TableType oldType, final TableType newType) {
        for (final ClientUpdateListener listener : listeners) {
            eventService.execute(new Runnable() {
                public void run() {
                    listener.onTableChangeType(table, oldType, newType);
                }
            });
        }
    }

    void fireNewTableEvent(final AirTable table) {
        for (final ClientUpdateListener listener : listeners) {
            eventService.execute(new Runnable() {
                public void run() {
                    listener.onNewTable(table);
                }
            });
        }
    }

    <T> void fireCallback(final Callback<T> callback, final T param) {
        eventService.execute(new Runnable() {
            public void run() {
                callback.run(param);
            }
        });
    }

    public AirTable getTable(final String tableName) {
        return airTableMap.get(tableName);
    }

    public boolean doesExist(final String tableName) {
        return airTableMap.containsKey(tableName);
    }

    public AirTable publishTable(final String tableName) {
        return null;
    }

    public void publishTable(final String tableName, final Callback<AirTable> callback) {
        AirTable alreadyExistingTable = airTableMap.get(tableName);
        if (alreadyExistingTable != null) {
            fireCallback(callback, alreadyExistingTable);
        }
        communications.sendPublishRequest(tableName);
        communications.run(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(TimeConstants.PUBLISH_WAIT_TIME);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    return;
                }
                AirTable table = airTableMap.get(tableName);
                if (table == null) {
                    table = new InternalAirTable(TableType.LOCAL);
                }
                fireCallback(callback, table);
            }
        });
    }

    public void addClientListener(final ClientUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeClientListener(final ClientUpdateListener listener) {
        listeners.remove(listener);
    }

    void onRemoteTableExists(String tableName) {
    }
}
