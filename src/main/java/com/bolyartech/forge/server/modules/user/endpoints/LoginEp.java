package com.bolyartech.forge.server.modules.user.endpoints;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.StringEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.handlers.db.SecureDbHandler;
import com.bolyartech.forge.server.misc.BasicResponseCodes;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.misc.Params;
import com.bolyartech.forge.server.modules.user.UserHandler;
import com.bolyartech.forge.server.modules.user.UserResponseCodes;
import com.bolyartech.forge.server.modules.user.data.ScreenName;
import com.bolyartech.forge.server.modules.user.data.User;
import com.bolyartech.forge.server.modules.user.data.SessionInfo;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import spark.Request;
import spark.Response;
import spark.Session;

import java.sql.Connection;
import java.sql.SQLException;


public class LoginEp extends StringEndpoint {
    public LoginEp(Handler<String> handler) {
        super(HttpMethod.POST, "login", handler);
    }


    public static class LoginHandler extends SecureDbHandler {
        private Gson mGson;


        public LoginHandler(DbPool dbPool) {
            super(dbPool);
            mGson = new Gson();
        }


        @Override
        public ForgeResponse handleWithDb(Request request, Response response, Connection dbc) throws SQLException {
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

                    Session sess = request.session();
                    sess.attribute(UserHandler.SESSION_VAR_NAME, user);


                    return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(),
                            mGson.toJson(new RokLogin(request.session().maxInactiveInterval(), si)));
                } else {
                    return new ForgeResponse(UserResponseCodes.Errors.INVALID_LOGIN.getCode(), "Invalid login");
                }
            } else {
                return new ForgeResponse(BasicResponseCodes.Errors.MISSING_PARAMETERS.getCode(), "Missing parameters");
            }
        }
    }


    public static class RokLogin {
        @SerializedName("session_ttl")
        public final int sessionTtl;
        @SerializedName("session_info")
        public final SessionInfo sessionInfo;


        public RokLogin(int sessionTtl, SessionInfo sessionInfo) {
            this.sessionTtl = sessionTtl;
            this.sessionInfo = sessionInfo;
        }
    }

}
