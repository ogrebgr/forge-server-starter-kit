package com.bolyartech.forge.server.modules.user.endpoints;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.StringEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.BasicResponseCodes;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.modules.user.UserHandler;
import com.bolyartech.forge.server.modules.user.UserResponseCodes;
import com.bolyartech.forge.server.modules.user.data.ScreenName;
import com.bolyartech.forge.server.modules.user.data.User;
import com.google.common.base.Strings;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class ScreenNameEp extends StringEndpoint {

    public ScreenNameEp(Handler<String> handler) {
        super(HttpMethod.POST, "screen_name", handler);
    }


    public static class ScreenNameHandler extends UserHandler {
        public ScreenNameHandler(DbPool dbPool) {
            super(dbPool);
        }


        @Override
        protected ForgeResponse handleLoggedIn(Request request,
                                               Response response,
                                               Connection dbc,
                                               User user) throws SQLException {

            String screenName = request.queryParams("screen_name".trim());

            if (Strings.isNullOrEmpty(screenName)) {
                return new ForgeResponse(BasicResponseCodes.Errors.MISSING_PARAMETERS.getCode(), "Missing parameters");
            }

            if (!ScreenName.isValid(screenName)) {
                return new ForgeResponse(UserResponseCodes.Errors.INVALID_SCREEN_NAME.getCode(), "Invalid screen name");
            }

            try {
                Statement lockSt = dbc.createStatement();
                lockSt.execute("LOCK TABLES users WRITE, user_screen_names WRITE");

                if (ScreenName.exists(dbc, screenName)) {
                    return new ForgeResponse(UserResponseCodes.Errors.SCREEN_NAME_EXISTS.getCode(), "Screen name exist");
                }


                ScreenName.setForUser(dbc, user.getId(), screenName);
                return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(), "OK");
            } finally {
                Statement unlockSt = dbc.createStatement();
                unlockSt.execute("UNLOCK TABLES");
            }
        }
    }
}
