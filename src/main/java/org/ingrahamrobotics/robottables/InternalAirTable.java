package org.ingrahamrobotics.robottables;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import org.ingrahamrobotics.robottables.api.AirTable;
import org.ingrahamrobotics.robottables.api.TableType;
import org.ingrahamrobotics.robottables.api.UpdateAction;
import org.ingrahamrobotics.robottables.api.listeners.TableUpdateListener;
import org.ingrahamrobotics.robottables.interfaces.InternalRobotTables;

public class InternalAirTable implements AirTable {

    private final InternalRobotTables robotTables;
    private final Hashtable valueMap = new Hashtable(); // Map from String to String
    private final List listeners = new ArrayList(); // List of TableUpdateListener
    private TableType type;
    private long lastUpdate;

    public InternalAirTable(final RobotTables tables, final TableType initialType) {
        robotTables = tables;
        this.type = initialType;
    }

    public TableType getType() {
        return type;
    }

    void setType(TableType type) {
        this.type = type;
    }

    public long getTimeSinceLastUpdate() {
        return System.currentTimeMillis() - lastUpdate;
    }

    void updatedNow() {
        lastUpdate = System.currentTimeMillis();
    }

    void sendUpdateKey(final String key, final String value, final UpdateAction action) {
        for (int i = 0; i < listeners.size(); i++) {
            final TableUpdateListener listener = (TableUpdateListener) listeners.get(i);
            robotTables.executeEvent(new Runnable() {
                public void run() {
                    listener.onUpdateKey(InternalAirTable.this, key, value, action);
                }
            });
        }
    }

    void sendDeleteKey(final String key) {
        for (int i = 0; i < listeners.size(); i++) {
            final TableUpdateListener listener = (TableUpdateListener) listeners.get(i);
            robotTables.executeEvent(new Runnable() {
                public void run() {
                    listener.onRemoveKey(InternalAirTable.this, key);
                }
            });
        }
    }

    public void addUpdateListener(final TableUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void remoteUpdateListener(final TableUpdateListener listener) {
        listeners.remove(listener);
    }

    public String get(final String key) {
        return (String) valueMap.get(key);
    }

    public String get(final String key, final String defaultValue) {
        String value = (String) valueMap.get(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    public int getInt(final String key) {
        String str = (String) valueMap.get(key);
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public int getInt(final String key, final int defaultValue) {
        String str = (String) valueMap.get(key);
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public double getDouble(final String key) {
        String str = (String) valueMap.get(key);
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    public double getDouble(final String key, final double defaultValue) {
        String str = (String) valueMap.get(key);
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public boolean getBoolean(final String key) {
        String str = (String) valueMap.get(key);
        try {
            return Boolean.parseBoolean(str);
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public boolean getBoolean(final String key, final boolean defaultValue) {
        String str = (String) valueMap.get(key);
        try {
            return Boolean.parseBoolean(str);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public long getLong(final String key) {
        String str = (String) valueMap.get(key);
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException ex) {
            return 0l;
        }
    }

    public long getLong(final String key, final long defaultValue) {
        String str = (String) valueMap.get(key);
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public boolean contains(final String key) {
        return valueMap.containsKey(key);
    }

    public boolean isInt(final String key) {
        String str = (String) valueMap.get(key);
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public boolean isDouble(final String key) {
        String str = (String) valueMap.get(key);
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public boolean isBoolean(final String key) {
        String str = (String) valueMap.get(key);
        try {
            Boolean.parseBoolean(str);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public void set(final String key, final String value) {
        if (value == null) {
            if (valueMap.containsKey(key)) {
                valueMap.remove(key);
                robotTables.tableKeyRemoved(this, key);
            }
        } else {
            String oldValue = (String) valueMap.get(key);
            if (oldValue == null || !value.equals(oldValue)) {
                valueMap.put(key, value);
                robotTables.tableUpdated(this);
            }
        }
    }

    public void clear() {
        if (!valueMap.isEmpty()) {
            valueMap.clear();
            robotTables.tableCleared(this);
        }
    }
}
