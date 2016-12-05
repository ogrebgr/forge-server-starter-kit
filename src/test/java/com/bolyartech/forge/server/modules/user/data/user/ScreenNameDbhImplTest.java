package com.bolyartech.forge.server.modules.user.data.user;

import com.bolyartech.forge.server.config.DbConfiguration;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.ServerTools;
import com.bolyartech.forge.server.modules.user.data.screen_name.ScreenName;
import com.bolyartech.forge.server.modules.user.data.screen_name.ScreenNameDbh;
import com.bolyartech.forge.server.modules.user.data.screen_name.ScreenNameDbhImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;


public class ScreenNameDbhImplTest {
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
        DbTools.deleteAllScreenNames(dbc);
        DbTools.deleteAllScrams(dbc);
        DbTools.deleteAllUsers(dbc);
        dbc.close();
    }

    @After
    public void after() throws SQLException {
        Connection dbc = mDbPool.getConnection();
        DbTools.deleteAllScreenNames(dbc);
        DbTools.deleteAllUsers(dbc);
        dbc.close();
    }


    @Test
    public void testCreate() throws SQLException {
        ScreenNameDbh dbh = new ScreenNameDbhImpl();
        UserDbh userDbh = new UserDbhImpl();

        Connection dbc = mDbPool.getConnection();
        User userNew = userDbh.createNew(dbc, true, UserLoginType.GOOGLE);

        ScreenName snNew = dbh.createNew(dbc, userNew.getId(), "some Screenname");
        ScreenName snLoaded = dbh.loadByUser(dbc, userNew.getId());
        assertTrue(snLoaded.equals(snNew));

        User userNew2 = userDbh.createNew(dbc, true, UserLoginType.GOOGLE);
        ScreenName snSecond = dbh.createNew(dbc, userNew2.getId(), "some Screenname");
        assertTrue("Duplicate screen name", snSecond == null);

        dbc.close();
    }


    @Test
    public void testChange() throws SQLException {
        ScreenNameDbh dbh = new ScreenNameDbhImpl();
        UserDbh userDbh = new UserDbhImpl();

        Connection dbc = mDbPool.getConnection();
        User userNew = userDbh.createNew(dbc, true, UserLoginType.GOOGLE);

        ScreenName snNew = dbh.createNew(dbc, userNew.getId(), "some Screenname");

        ScreenName snChanged = dbh.change(dbc, snNew, "new screenname");

        dbc.close();
        assertTrue(snChanged.getScreenName().equals("new screenname"));
    }


    @Test
    public void testExists() throws SQLException {
        ScreenNameDbh dbh = new ScreenNameDbhImpl();
        UserDbh userDbh = new UserDbhImpl();

        Connection dbc = mDbPool.getConnection();
        assertTrue("Found when it should not", !dbh.exists(dbc, "test"));
        User userNew = userDbh.createNew(dbc, true, UserLoginType.GOOGLE);

        ScreenName snNew = dbh.createNew(dbc, userNew.getId(), "test");
        assertTrue("Not found when it should", dbh.exists(dbc, "test"));

        dbc.close();
    }
}
