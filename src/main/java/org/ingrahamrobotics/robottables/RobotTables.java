package org.ingrahamrobotics.robottables;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.ingrahamrobotics.robottables.api.AirTable;
import org.ingrahamrobotics.robottables.api.RobotTablesClient;
import org.ingrahamrobotics.robottables.api.TableCallback;
import org.ingrahamrobotics.robottables.api.TableType;
import org.ingrahamrobotics.robottables.api.listeners.ClientUpdateListener;
import org.ingrahamrobotics.robottables.interfaces.InternalRobotTables;

public class RobotTables implements RobotTablesClient, InternalRobotTables {

    ExecutorService eventService = Executors.newSingleThreadExecutor();
    private Hashtable airTableMap = new Hashtable(); // Map from String to InternalAirTable
    private List listeners = new ArrayList(); // List of ClientUpdateListener
    private final TableCommunications communications;

    public RobotTables(final int[] ports, final int port) {
        communications = new TableCommunications(ports, port);
    }

    InternalAirTable externalPublishedTable(final String tableName) {
        InternalAirTable airTable = (InternalAirTable) airTableMap.get(tableName);
        if (airTable != null) {
            airTable.clear(); // TODO: Should we clear all values when a table is re-published?
            if (airTable.getType() != TableType.REMOTE) {
                TableType oldType = airTable.getType();
                airTable.setType(TableType.REMOTE);
                fireTableTypeChangeEvent(airTable, oldType, TableType.REMOTE);
            }
        } else {
            airTable = new InternalAirTable(this, TableType.REMOTE);
            airTableMap.put(tableName, airTable);
            fireNewTableEvent(airTable);
        }
        return airTable;
    }

    public void tableUpdated(InternalAirTable table) {
    }

    public void tableKeyRemoved(InternalAirTable table, String key) {
    }

    public void tableCleared(InternalAirTable table) {
    }

    public void executeEvent(final Runnable runnable) {

    }

    void fireTableTypeChangeEvent(final AirTable table, final TableType oldType, final TableType newType) {
        for (int i = 0; i < listeners.size(); i++) {
            final ClientUpdateListener listener = (ClientUpdateListener) listeners.get(i);
            eventService.execute(new Runnable() {
                public void run() {
                    listener.onTableChangeType(table, oldType, newType);
                }
            });
        }
    }

    void fireNewTableEvent(final AirTable table) {
        for (int i = 0; i < listeners.size(); i++) {
            final ClientUpdateListener listener = (ClientUpdateListener) listeners.get(i);
            eventService.execute(new Runnable() {
                public void run() {
                    listener.onNewTable(table);
                }
            });
        }
    }

    void fireCallback(final TableCallback callback, final AirTable param) {
        eventService.execute(new Runnable() {
            public void run() {
                callback.run(param);
            }
        });
    }

    public AirTable getTable(final String tableName) {
        return (InternalAirTable) airTableMap.get(tableName);
    }

    public boolean doesExist(final String tableName) {
        return airTableMap.containsKey(tableName);
    }

    public AirTable publishTable(final String tableName) {
        return null;
    }

    public void publishTable(final String tableName, final TableCallback callback) {
        AirTable alreadyExistingTable = (InternalAirTable) airTableMap.get(tableName);
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
                AirTable table = (InternalAirTable) airTableMap.get(tableName);
                if (table == null) {
                    table = new InternalAirTable(RobotTables.this, TableType.LOCAL);
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
}
