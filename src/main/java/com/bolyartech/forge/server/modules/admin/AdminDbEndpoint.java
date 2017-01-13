package com.bolyartech.forge.server.modules.admin;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.handler.ForgeDbSecureEndpoint;
import com.bolyartech.forge.server.modules.admin.data.AdminUser;
import com.bolyartech.forge.server.modules.user.UserResponseCodes;
import com.bolyartech.forge.server.response.ResponseException;
import com.bolyartech.forge.server.response.forge.ForgeResponse;
import com.bolyartech.forge.server.route.RequestContext;
import com.bolyartech.forge.server.session.Session;

import java.sql.Connection;
import java.sql.SQLException;


abstract public class AdminDbEndpoint extends ForgeDbSecureEndpoint {

    public AdminDbEndpoint(DbPool dbPool) {
        super(dbPool);
    }


    abstract public ForgeResponse handle(RequestContext ctx,
                                         Connection dbc,
                                         AdminUser user) throws ResponseException, SQLException;


    @Override
    public ForgeResponse handleForgeSecure(RequestContext ctx, Connection dbc)
            throws ResponseException, SQLException {

        Session session = ctx.getSession();
        AdminUser user = session.getVar(SessionVars.VAR_USER);
        if (user != null) {
            return handle(ctx, dbc, user);
        } else {
            return new ForgeResponse(UserResponseCodes.Errors.NOT_LOGGED_IN, "Not logged in");
        }
    }
}
