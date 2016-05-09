package com.bolyartech.forge.server.skeleton.misc;

import com.bolyartech.forge.server.HandlerException;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.misc.ForgeSecureHandler;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

abstract public class DbHandler extends ForgeSecureHandler {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private final DbPool mDbPool;


    abstract protected ForgeResponse handleForgeSecure(Request request, Response response, Connection dbc) throws SQLException;


    public DbHandler(DbPool dbPool) {
        mDbPool = dbPool;
    }


    @Override
    public ForgeResponse handleForgeSecure(Request request, Response response) throws HandlerException {
        try {
            Connection dbc = mDbPool.getConnection();
            ForgeResponse ret = handleForgeSecure(request, response, dbc);
            dbc.close();

            return ret;
        } catch (SQLException e) {
            mLogger.error("DB error {}", e);
            throw new HandlerException(MessageFormat.format("Error in handleSecure() with DB: {0}", e));
        }
    }
}