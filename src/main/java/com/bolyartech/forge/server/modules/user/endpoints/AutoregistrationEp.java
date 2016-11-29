package com.bolyartech.forge.server.modules.user.endpoints;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.StringEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.handlers.db.SecureDbHandler;
import com.bolyartech.forge.server.misc.BasicResponseCodes;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.modules.user.data.RokResponseAutoregistration;
import com.bolyartech.forge.server.modules.user.data.User;
import com.bolyartech.forge.server.modules.user.data.SessionInfo;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Session;

import java.sql.Connection;
import java.sql.SQLException;

public class AutoregistrationEp extends StringEndpoint {
    public AutoregistrationEp(Handler<String> handler) {
        super(HttpMethod.POST, "autoregister", handler);
    }


    public static class UserAutoregistrationHandler extends SecureDbHandler {
        private final Gson mGson;

        public UserAutoregistrationHandler(DbPool dbPool) {
            super(dbPool);
            mGson = new Gson();
        }


        @Override
        public ForgeResponse handleWithDb(Request request, Response response, Connection dbc) throws SQLException {
            Session sess = request.session();

            User.AnonymousUserHelper auser = User.generateAnonymousUser(dbc);

            SessionInfo si = new SessionInfo(auser.mUser.getId(), null, "user" + auser.mUser.getId());

            return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(),
                    mGson.toJson(new RokResponseAutoregistration(auser.mUser.getUsername(),
                    auser.mClearPassword,
                    sess.maxInactiveInterval(),
                    si
            )));
        }
    }
}
