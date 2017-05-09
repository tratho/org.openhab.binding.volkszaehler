package org.openhab.binding.volkszaehler.internal;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLReader {
    public ResultSet getResultSet(String sql) throws SQLException;

    public void removeListener(SQLReaderListener listener);

    public void addListener(SQLReaderListener listener);

    public void callAllListener();
}
