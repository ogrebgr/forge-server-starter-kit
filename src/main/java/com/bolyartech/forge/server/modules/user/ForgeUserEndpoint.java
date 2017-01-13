package com.bolyartech.forge.server.modules.user;

import com.bolyartech.forge.server.handler.ForgeSecureEndpoint;
import com.bolyartech.forge.server.modules.user.data.user.User;
import com.bolyartech.forge.server.response.ResponseException;
import com.bolyartech.forge.server.response.forge.ForgeResponse;
import com.bolyartech.forge.server.route.RequestContext;
import com.bolyartech.forge.server.session.Session;


abstract public class ForgeUserEndpoint extends ForgeSecureEndpoint {
    abstract public ForgeResponse handle(RequestContext ctx,
                                         Session session,
                                         User user) throws ResponseException;


    @Override
    public ForgeResponse handleForgeSecure(RequestContext ctx) throws ResponseException {
        Session session = ctx.getSession();
        User user = session.getVar(SessionVars.VAR_USER);
        if (user != null) {
            return handle(ctx, session, user);
        } else {
            return new ForgeResponse(UserResponseCodes.Errors.NOT_LOGGED_IN, "Not logged in");
        }
    }
}
