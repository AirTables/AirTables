package org.ingrahamrobotics.robottables.interfaces;

import org.ingrahamrobotics.robottables.InternalAirTable;

public interface InternalRobotTables {

    public void tableUpdated(InternalAirTable table);

    public void tableKeyRemoved(InternalAirTable table, String key);

    public void tableCleared(InternalAirTable table);

    public void executeEvent(Runnable runnable);
}
