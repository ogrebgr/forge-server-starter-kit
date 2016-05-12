package com.bolyartech.forge.server.skeleton.modules.api.register;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.SimpleEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.misc.Params;
import com.bolyartech.forge.server.skeleton.data.ScreenName;
import com.bolyartech.forge.server.skeleton.data.User;
import com.bolyartech.forge.server.skeleton.misc.DbHandler;
import com.bolyartech.forge.server.skeleton.modules.api.ResponseCodes;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UserRegistrationEp extends SimpleEndpoint {
    public UserRegistrationEp(Handler<String> handler) {
        super(HttpMethod.POST, "/api/user/register", handler);
    }


    public static class RegistrationHandler extends DbHandler {
        public RegistrationHandler(DbPool dbPool) {
            super(dbPool);
        }


        @Override
        protected ForgeResponse handleForgeSecure(Request request, Response response, Connection dbc) throws SQLException {
            String username = request.queryParams("username").trim();
            String password = request.queryParams("password");
            String screenName = request.queryParams("screen_name".trim());

            if (Params.areAllPresent(username, password, screenName)) {
                if (username.startsWith("$")) {
                    return new ForgeResponse(ResponseCodes.Errors.REGISTRATION_REFUSED.getCode(), "Registration refused");
                }

                if (!User.isValidUsername(username)) {
                    return new ForgeResponse(ResponseCodes.Errors.INVALID_USERNAME.getCode(), "Invalid username");
                }

                if (!ScreenName.isValid(screenName)) {
                    return new ForgeResponse(ResponseCodes.Errors.INVALID_SCREEN_NAME.getCode(), "Invalid screen name");
                }


                try {
                    Statement lockSt = dbc.createStatement();
                    lockSt.execute("LOCK TABLES users WRITE, user_screen_names WRITE");

                    if (User.usernameExists(dbc, username)) {
                        return new ForgeResponse(ResponseCodes.Errors.USERNAME_EXISTS.getCode(), "username taken");
                    }

                    if (ScreenName.exists(dbc, screenName)) {
                        return new ForgeResponse(ResponseCodes.Errors.SCREEN_NAME_EXISTS.getCode(), "Screen name exist");
                    }

                    User.createNew(dbc, username, password, false, screenName);
                    return new ForgeResponse(ResponseCodes.Oks.OK.getCode(), "OK");
                } finally {
                    Statement unlockSt = dbc.createStatement();
                    unlockSt.execute("UNLOCK TABLES");
                }
            } else {
                return new ForgeResponse(ResponseCodes.Errors.MISSING_PARAMETERS.getCode(), "Missing parameters");
            }
        }
    }
}
