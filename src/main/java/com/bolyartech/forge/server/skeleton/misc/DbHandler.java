package com.bolyartech.forge.server.skeleton.misc;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HandlerException;
import com.bolyartech.forge.server.db.DbPool;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;

abstract public class DbHandler implements Handler<String> {
    private final DbPool mDbPool;


    abstract protected String handle(Request request, Response response, Connection dbc) throws SQLException;


    public DbHandler(DbPool dbPool) {
        mDbPool = dbPool;
    }


    @Override
    public String handle(Request request, Response response) throws HandlerException {
        try {
            Connection dbc = mDbPool.getConnection();
            String ret = handle(request, response, dbc);
            dbc.close();

            return ret;
        } catch (SQLException e) {
            throw new HandlerException(MessageFormat.format("Error in handle() with DB: {0}", e));
        }
    }
}