package com.bolyartech.forge.server.modules.user;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.handlers.db.SecureDbHandler;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.modules.user.data.User;
import spark.Request;
import spark.Response;
import spark.Session;

import java.sql.Connection;
import java.sql.SQLException;

abstract public class UserHandler extends SecureDbHandler {
    public static final String SESSION_VAR_NAME = "user";

    public UserHandler(DbPool dbPool) {
        super(dbPool);
    }


    abstract protected ForgeResponse handleLoggedIn(Request request, Response response, Connection dbc, User user) throws SQLException;


    @Override
    public ForgeResponse handleWithDb(Request request, Response response, Connection dbc) throws SQLException {
        Session sess = request.session();
        User user = sess.attribute(SESSION_VAR_NAME);

        if (user != null) {
            return handleLoggedIn(request, response, dbc, user);
        } else {
            return new ForgeResponse(UserResponseCodes.Errors.NOT_LOGGED_IN.getCode(), "Invalid login");
        }
    }
}