package com.bolyartech.forge.server.modules.user.data.scram;

import java.sql.Connection;
import java.sql.SQLException;


public interface ScramDbh {
    Scram loadByUser(Connection dbc, long user) throws SQLException;
    Scram loadByUsername(Connection dbc, String username) throws SQLException;

    Scram createNew(Connection dbc, long user, String username, String salt, String serverKey, String storedKey, int iterations)
            throws SQLException;
    Scram change(Connection dbc, Scram scram, String salt, String serverKey, String storedKey, int iterations)
            throws SQLException;
    boolean delete(Connection dbc, Scram scram) throws SQLException;
}
