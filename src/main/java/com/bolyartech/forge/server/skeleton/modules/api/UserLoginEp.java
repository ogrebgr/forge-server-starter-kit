package com.bolyartech.forge.server.skeleton.modules.api;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HandlerException;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.SimpleEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.skeleton.data.User;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;


public class UserLoginEp extends SimpleEndpoint {
    public UserLoginEp(Handler<String> handler) {
        super(HttpMethod.GET, "login", handler);
    }


    public static class UserLoginHandler implements Handler<String> {
        private final DbPool mDbPool;


        public UserLoginHandler(DbPool dbPool) {
            mDbPool = dbPool;
        }


        @Override
        public String handle(Request request, Response response) throws HandlerException {
            try {

                Connection dbc = mDbPool.getConnection();
                try {
                    User user = User.loadById(dbc, 1);
                    return user.getUsername();
                } finally {
                    dbc.close();
                }
            } catch (SQLException e) {
                throw new HandlerException(e);
            }
        }
    }
}
