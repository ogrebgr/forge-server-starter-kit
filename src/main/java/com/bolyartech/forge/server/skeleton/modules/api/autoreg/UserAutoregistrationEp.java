package com.bolyartech.forge.server.skeleton.modules.api.autoreg;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.SimpleEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.skeleton.data.User;
import com.bolyartech.forge.server.skeleton.json.SessionInfo;
import com.bolyartech.forge.server.skeleton.misc.DbHandler;
import com.bolyartech.forge.server.skeleton.modules.api.ResponseCodes;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Session;

import java.sql.Connection;
import java.sql.SQLException;

public class UserAutoregistrationEp extends SimpleEndpoint {
    public UserAutoregistrationEp(Handler<String> handler) {
        super(HttpMethod.POST, "/api/user/autoregister", handler);
    }


    public static class UserAutoregistrationHandler extends DbHandler {
        private Gson mGson;

        public UserAutoregistrationHandler(DbPool dbPool) {
            super(dbPool);
            mGson = new Gson();
        }


        @Override
        protected ForgeResponse handleForgeSecure(Request request, Response response, Connection dbc) throws SQLException {
            Session sess = request.session();

            User user = User.generateAnonymousUser(dbc);

            SessionInfo si = new SessionInfo(user.getId(), "");

            return new ForgeResponse(ResponseCodes.Oks.OK.getCode(),
                    mGson.toJson(new RokResponseAutoregistration(user.getUsername(),
                    user.getEncryptedPassword(),
                    sess.maxInactiveInterval(),
                    si
            )));
        }
    }
}
