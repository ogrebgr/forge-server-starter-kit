package com.bolyartech.forge.server.modules.user.data.user_ext_id;

import java.sql.Connection;
import java.sql.SQLException;


public interface UserExtIdDbh {
    UserExtId createNew(Connection dbc, long user, String extId, UserExtId.Type type) throws SQLException;

    UserExtId loadByUser(Connection dbc, long user, UserExtId.Type type) throws SQLException;

    UserExtId loadByExtId(Connection dbc, String extId, UserExtId.Type type) throws SQLException;
}
