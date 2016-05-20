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


public class UserRegistrationPostAutoEp extends StringEndpoint {
    public UserRegistrationPostAutoEp(Handler<String> handler) {
        super(HttpMethod.POST, "register_postauto", handler);
    }


    public static class UserRegistrationPostAutoHandler extends SecureDbHandler {
        public UserRegistrationPostAutoHandler(DbPool dbPool) {
            super(dbPool);
        }


        @Override
        public ForgeResponse handleWithDb(Request request, Response response, Connection dbc) throws SQLException {
            String username = request.queryParams("username").trim();
            String password = request.queryParams("password");
            String newUsername = request.queryParams("new_username").trim();
            String newPassword = request.queryParams("new_password");
            String screenName = request.queryParams("screen_name".trim());

            if (Params.areAllPresent(username, password, newUsername, newPassword, screenName)) {
                if (username.startsWith("$")) {
                    return new ForgeResponse(UserResponseCodes.Errors.REGISTRATION_REFUSED.getCode(), "Registration refused");
                }

                User user = User.checkLogin(dbc, username, password);
                if (user == null) {
                    return new ForgeResponse(UserResponseCodes.Errors.INVALID_LOGIN.getCode(), "Invalid login");
                }

                if (!User.isValidUsername(newUsername)) {
                    return new ForgeResponse(UserResponseCodes.Errors.INVALID_USERNAME.getCode(), "Invalid username");
                }

                if (!ScreenName.isValid(screenName)) {
                    return new ForgeResponse(UserResponseCodes.Errors.INVALID_SCREEN_NAME.getCode(), "Invalid screen name");
                }

                try {
                    Statement lockSt = dbc.createStatement();
                    lockSt.execute("LOCK TABLES users WRITE, user_screen_names WRITE");

                    if (User.usernameExists(dbc, newUsername)) {
                        return new ForgeResponse(UserResponseCodes.Errors.USERNAME_EXISTS.getCode(), "username taken");
                    }

                    if (ScreenName.exists(dbc, screenName)) {
                        return new ForgeResponse(UserResponseCodes.Errors.SCREEN_NAME_EXISTS.getCode(), "Screen name exist");
                    }

                    user.auto2registered(dbc, newUsername, newPassword, screenName);
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
