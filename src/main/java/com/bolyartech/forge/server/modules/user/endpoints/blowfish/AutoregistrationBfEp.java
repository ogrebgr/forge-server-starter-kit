package com.bolyartech.forge.server.modules.user.endpoints.blowfish;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.handler.ForgeDbEndpoint;
import com.bolyartech.forge.server.modules.user.LoginType;
import com.bolyartech.forge.server.modules.user.SessionVars;
import com.bolyartech.forge.server.modules.user.data.RokResponseAutoregistration;
import com.bolyartech.forge.server.modules.user.data.SessionInfo;
import com.bolyartech.forge.server.modules.user.data.blowfish.BlowfishDbh;
import com.bolyartech.forge.server.modules.user.data.user.UserDbh;
import com.bolyartech.forge.server.modules.user.data.user_blowfish.UserBlowfish;
import com.bolyartech.forge.server.modules.user.data.user_blowfish.UserBlowfishDbh;
import com.bolyartech.forge.server.response.ResponseException;
import com.bolyartech.forge.server.response.forge.ForgeResponse;
import com.bolyartech.forge.server.response.forge.OkResponse;
import com.bolyartech.forge.server.route.RequestContext;
import com.bolyartech.forge.server.session.Session;
import com.google.gson.Gson;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;


public class AutoregistrationBfEp extends ForgeDbEndpoint {
    private final Gson mGson;

    private final UserDbh mUserDbh;
    private final BlowfishDbh mBlowfishDbh;
    private final UserBlowfishDbh mUserBlowfishDbh;


    public AutoregistrationBfEp(DbPool dbPool, UserDbh userDbh, BlowfishDbh scramDbh, UserBlowfishDbh userBlowfishDbh) {
        super(dbPool);
        mGson = new Gson();
        mUserDbh = userDbh;
        mBlowfishDbh = scramDbh;
        mUserBlowfishDbh = userBlowfishDbh;
    }


    @Override
    public ForgeResponse handleForge(RequestContext ctx, Connection dbc) throws ResponseException,
            SQLException {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[24];
        random.nextBytes(salt);

        String username;
        String password = UUID.randomUUID().toString();
        UserBlowfish us;

        while (true) {
            // adding "g" as a prefix in order to make the username valid when UUID starts with number
            username = "g" + UUID.randomUUID().toString().replace("-", "");

            us = mUserBlowfishDbh.createNew(dbc, mUserDbh, mBlowfishDbh, username, password);
            if (us != null) {
                break;
            }
        }


        SessionInfo si = new SessionInfo(us.getUser().getId(), null);

        Session session = ctx.getSession();
        session.setVar(SessionVars.VAR_USER, us.getUser());
        session.setVar(SessionVars.VAR_LOGIN_TYPE, LoginType.NATIVE);

        return new OkResponse(
                mGson.toJson(new RokResponseAutoregistration(username,
                        password,
                        session.getMaxInactiveInterval(),
                        si
                )));
    }
}
