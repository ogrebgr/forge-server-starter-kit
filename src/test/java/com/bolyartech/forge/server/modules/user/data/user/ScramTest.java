package com.bolyartech.forge.server.modules.user.data.user;

import com.bolyartech.forge.server.config.DbConfiguration;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.ServerTools;
import com.bolyartech.forge.server.modules.user.data.scram.Scram;
import com.bolyartech.forge.server.modules.user.data.scram.ScramDbh;
import com.bolyartech.forge.server.modules.user.data.scram.ScramDbhImpl;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;


public class ScramTest {
    private DbPool mDbPool;


    @Before
    public void setup() throws SQLException {
        if (mDbPool == null) {
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(classLoader.getResource("db.conf").getFile());

            DbConfiguration dbConf = ServerTools.loadDbConf(file);

            mDbPool = ServerTools.createComboPooledDataSource(dbConf);
        }

        Connection dbc = mDbPool.getConnection();
        DbTools.deleteAllScrams(dbc);
        dbc.close();
    }


    @Test
    public void testCreateNew() throws SQLException {
        ScramDbh dbh = new ScramDbhImpl();

        Connection dbc = mDbPool.getConnection();
        Scram scrNew = dbh.createNew(dbc, 1, "username", "salt", "server_key", "stored_key", 11);
        Scram scrLoaded = dbh.loadByUser(dbc, 1);
        dbc.close();

        assertTrue("Created and loaded are different", scrLoaded.equals(scrNew));
    }


    @Test
    public void testLoadByUsername() throws SQLException {
        ScramDbh dbh = new ScramDbhImpl();

        Connection dbc = mDbPool.getConnection();
        Scram scrNew = dbh.createNew(dbc, 1, "username", "salt", "server_key", "stored_key", 11);
        Scram scrLoaded = dbh.loadByUsername(dbc, "username");
        dbc.close();

        assertTrue("Created and loaded are different", scrLoaded.equals(scrNew));
    }


    @Test
    public void testChange() throws SQLException {
        ScramDbh dbh = new ScramDbhImpl();

        Connection dbc = mDbPool.getConnection();
        Scram scrNew = dbh.createNew(dbc, 1, "username", "salt", "server_key", "stored_key", 11);
        Scram scrChanged = dbh.change(dbc, scrNew, "salt2", "server_key2", "stored_key2", 12);
        Scram scrLoaded = dbh.loadByUsername(dbc, "username");
        dbc.close();

        assertTrue("Changed and loaded are different", scrLoaded.equals(scrChanged));
    }


    @Test
    public void testDelete() throws SQLException {
        ScramDbh dbh = new ScramDbhImpl();

        Connection dbc = mDbPool.getConnection();
        Scram scrNew = dbh.createNew(dbc, 1, "username", "salt", "server_key", "stored_key", 11);
        boolean deleted = dbh.delete(dbc, scrNew);
        Scram nonExisting = dbh.loadByUser(dbc, 1);
        dbc.close();

        assertTrue("Exists when it should not", nonExisting == null);
    }

}
