package net.daboross.airtables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.daboross.airtables.api.AirTable;
import net.daboross.airtables.api.TableType;
import net.daboross.airtables.api.listeners.TableUpdateListener;

public class InternalAirTable implements AirTable {

    private final Map<String, String> valueMap = new HashMap<String, String>();
    private final List<TableUpdateListener> listeners = new ArrayList<TableUpdateListener>();
    private TableType type;
    private long lastUpdate;

    public InternalAirTable(final TableType initialType) {
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

    public void addUpdateListener(final TableUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void remoteUpdateListener(final TableUpdateListener listener) {
        listeners.remove(listener);
    }

    public String get(final String key) {
        return valueMap.get(key);
    }

    public String get(final String key, final String defaultValue) {
        String value = valueMap.get(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    public int getInt(final String key) {
        String str = valueMap.get(key);
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    public int getInt(final String key, final int defaultValue) {
        String str = valueMap.get(key);
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public double getDouble(final String key) {
        String str = valueMap.get(key);
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    public double getDouble(final String key, final double defaultValue) {
        String str = valueMap.get(key);
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public boolean getBoolean(final String key) {
        String str = valueMap.get(key);
        try {
            return Boolean.parseBoolean(str);
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public boolean getBoolean(final String key, final boolean defaultValue) {
        String str = valueMap.get(key);
        try {
            return Boolean.parseBoolean(str);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public long getLong(final String key) {
        return 0;
    }

    public long getLong(final String key, final long defaultValue) {
        return 0;
    }

    public boolean contains(final String key) {
        return false;
    }

    public boolean isInt(final String key) {
        return false;
    }

    public boolean isDouble(final String key) {
        return false;
    }

    public boolean isBoolean(final String key) {
        return false;
    }

    public void set(final String key, final String value) {

    }

    public Set<String> getKeys() {
        return null;
    }

    public Set<String> getValues() {
        return null;
    }

    public Map<String, String> asMap() {
        return null;
    }

    public void clear() {

    }
}
