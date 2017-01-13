package com.bolyartech.forge.server.modules.user;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.handler.ForgeDbSecureEndpoint;
import com.bolyartech.forge.server.modules.user.data.user.User;
import com.bolyartech.forge.server.response.ResponseException;
import com.bolyartech.forge.server.response.forge.ForgeResponse;
import com.bolyartech.forge.server.route.RequestContext;
import com.bolyartech.forge.server.session.Session;

import java.sql.Connection;
import java.sql.SQLException;


abstract public class ForgeUserDbEndpoint extends ForgeDbSecureEndpoint {


    public ForgeUserDbEndpoint(DbPool dbPool) {
        super(dbPool);
    }


    abstract public ForgeResponse handle(RequestContext ctx,
                                         Connection dbc,
                                         User user) throws ResponseException, SQLException;


    @Override
    public ForgeResponse handleForgeSecure(RequestContext ctx, Connection dbc)
            throws ResponseException, SQLException {

        Session session = ctx.getSession();
        User user = session.getVar(SessionVars.VAR_USER);
        if (user != null) {
            return handle(ctx, dbc, user);
        } else {
            return new ForgeResponse(UserResponseCodes.Errors.NOT_LOGGED_IN, "Not logged in");
        }
    }
}
