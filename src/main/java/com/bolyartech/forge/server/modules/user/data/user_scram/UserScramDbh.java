package com.bolyartech.forge.server.modules.user.data.user_scram;

import com.bolyartech.forge.server.modules.user.data.scram.ScramDbh;
import com.bolyartech.forge.server.modules.user.data.user.UserDbh;

import java.sql.Connection;
import java.sql.SQLException;


public interface UserScramDbh {
    UserScram createNew(Connection dbc,
                        UserDbh userDbh,
                        ScramDbh scramDbh,
                        String username,
                        String salt,
                        String serverKey,
                        String storedKey,
                        int iterations) throws SQLException;


}
