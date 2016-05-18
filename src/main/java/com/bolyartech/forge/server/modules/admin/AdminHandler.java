package com.bolyartech.forge.server.modules.admin;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.handlers.db.SecureDbHandler;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.modules.admin.data.AdminUser;
import com.bolyartech.forge.server.modules.user.UserResponseCodes;
import spark.Request;
import spark.Response;
import spark.Session;

import java.sql.Connection;
import java.sql.SQLException;


abstract public class AdminHandler extends SecureDbHandler {
    public static final String SESSION_VAR_NAME = "admin_user";

    public AdminHandler(DbPool dbPool) {
        super(dbPool);
    }


    abstract protected ForgeResponse handleAdmin(Request request, Response response, Connection dbc, AdminUser user) throws SQLException;


    @Override
    public ForgeResponse handleWithDb(Request request, Response response, Connection dbc) throws SQLException {
        Session sess = request.session();
        AdminUser user = sess.attribute(SESSION_VAR_NAME);

        if (user != null) {
            return handleAdmin(request, response, dbc, user);
        } else {
            return new ForgeResponse(UserResponseCodes.Errors.NOT_LOGGED_IN.getCode(), "Invalid login");
        }
    }
}
