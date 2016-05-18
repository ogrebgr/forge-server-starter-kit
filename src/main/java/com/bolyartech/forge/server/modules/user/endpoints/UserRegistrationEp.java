package com.bolyartech.forge.server.modules.user.endpoints;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.StringEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.handlers.db.SecureDbHandler;
import com.bolyartech.forge.server.misc.BasicResponseCodes;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.misc.Params;
import com.bolyartech.forge.server.modules.user.UserResponseCodes;
import com.bolyartech.forge.server.modules.user.data.ScreenName;
import com.bolyartech.forge.server.modules.user.data.User;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UserRegistrationEp extends StringEndpoint {
    public UserRegistrationEp(Handler<String> handler) {
        super(HttpMethod.POST, "/api/user/register", handler);
    }


    public static class RegistrationHandler extends SecureDbHandler {
        public RegistrationHandler(DbPool dbPool) {
            super(dbPool);
        }


        @Override
        public ForgeResponse handleWithDb(Request request, Response response, Connection dbc) throws SQLException {
            String username = request.queryParams("username").trim();
            String password = request.queryParams("password");
            String screenName = request.queryParams("screen_name".trim());

            if (Params.areAllPresent(username, password, screenName)) {
                if (username.startsWith("$")) {
                    return new ForgeResponse(UserResponseCodes.Errors.REGISTRATION_REFUSED.getCode(), "Registration refused");
                }

                if (!User.isValidUsername(username)) {
                    return new ForgeResponse(UserResponseCodes.Errors.INVALID_USERNAME.getCode(), "Invalid username");
                }

                if (!ScreenName.isValid(screenName)) {
                    return new ForgeResponse(UserResponseCodes.Errors.INVALID_SCREEN_NAME.getCode(), "Invalid screen name");
                }


                try {
                    Statement lockSt = dbc.createStatement();
                    lockSt.execute("LOCK TABLES users WRITE, user_screen_names WRITE");

                    if (User.usernameExists(dbc, username)) {
                        return new ForgeResponse(UserResponseCodes.Errors.USERNAME_EXISTS.getCode(), "username taken");
                    }

                    if (ScreenName.exists(dbc, screenName)) {
                        return new ForgeResponse(UserResponseCodes.Errors.SCREEN_NAME_EXISTS.getCode(), "Screen name exist");
                    }

                    User.createNew(dbc, username, password, false, screenName);
                    return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(), "OK");
                } finally {
                    Statement unlockSt = dbc.createStatement();
                    unlockSt.execute("UNLOCK TABLES");
                }
            } else {
                return new ForgeResponse(BasicResponseCodes.Errors.MISSING_PARAMETERS.getCode(), "Missing parameters");
            }
        }
    }
}
