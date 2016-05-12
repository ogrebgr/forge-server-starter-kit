package com.bolyartech.forge.server.skeleton.modules.api.login;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.SimpleEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.misc.Params;
import com.bolyartech.forge.server.skeleton.data.ScreenName;
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


public class LoginEp extends SimpleEndpoint {
    public LoginEp(Handler<String> handler) {
        super(HttpMethod.POST, "/api/user/login", handler);
    }


    public static class LoginHandler extends DbHandler {
        private Gson mGson;


        public LoginHandler(DbPool dbPool) {
            super(dbPool);
            mGson = new Gson();
        }


        @Override
        protected ForgeResponse handleForgeSecure(Request request, Response response, Connection dbc) throws SQLException {
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            if (Params.areAllPresent(username, password)) {
                User user = User.checkLogin(dbc, username, password);
                if (user != null) {
                    ScreenName sn = ScreenName.loadByUser(dbc, user.getId());

                    String screenName;
                    if (sn != null) {
                        screenName = sn.getScreenName();
                    } else {
                        screenName = ScreenName.createDefault(user.getId());
                    }

                    SessionInfo si = new SessionInfo(user.getId(), screenName);

                    return new ForgeResponse(ResponseCodes.Oks.OK.getCode(),
                            mGson.toJson(new RokLogin(request.session().maxInactiveInterval(), si)));
                } else {
                    return new ForgeResponse(ResponseCodes.Errors.INVALID_LOGIN.getCode(), "Invalid login");
                }
            } else {
                return new ForgeResponse(ResponseCodes.Errors.MISSING_PARAMETERS.getCode(), "Missing parameters");
            }
        }
    }
}
