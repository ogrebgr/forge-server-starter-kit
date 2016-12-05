package com.bolyartech.forge.server.modules.user.data.user_scram;

import com.bolyartech.forge.server.modules.user.data.scram.Scram;
import com.bolyartech.forge.server.modules.user.data.scram.ScramDbh;
import com.bolyartech.forge.server.modules.user.data.user.User;
import com.bolyartech.forge.server.modules.user.data.user.UserDbh;
import com.bolyartech.forge.server.modules.user.data.user.UserLoginType;

import java.sql.Connection;
import java.sql.SQLException;


public class UserScramDbhImpl implements UserScramDbh {
    @Override
    public UserScram createNew(Connection dbc,
                               UserDbh userDbh,
                               ScramDbh scramDbh,
                               String username,
                               String salt,
                               String serverKey,
                               String storedKey,
                               int iterations) throws SQLException {

        User user = userDbh.createNew(dbc, false, UserLoginType.SCRAM);
        Scram scram = scramDbh.createNew(dbc, user.getId(), username, salt, serverKey, storedKey, iterations);

        return new UserScram(user, scram);
    }
}
