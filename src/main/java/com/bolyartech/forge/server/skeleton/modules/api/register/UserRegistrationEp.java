package com.bolyartech.forge.server.skeleton.modules.api.register;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.SimpleEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.misc.Params;
import com.bolyartech.forge.server.skeleton.misc.DbHandler;
import com.google.common.base.Strings;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;

public class UserRegistrationEp extends SimpleEndpoint {
    public UserRegistrationEp(Handler<String> handler) {
        super(HttpMethod.POST, "/register", handler);
    }


    public static class RegistrationHandler extends DbHandler {
        public RegistrationHandler(DbPool dbPool) {
            super(dbPool);
        }


        @Override
        protected ForgeResponse handleForgeSecure(Request request, Response response, Connection dbc) throws SQLException {
            String username = request.params("username");
            String password = request.params("password");
            String newUsername = request.params("new_username");
            String newPassword = request.params("new_password");
            String screenName = request.params("screenName");

            if (!Params.allAllPresent(username, password, newUsername, newPassword, screenName)) {

            }


            return new ForgeResponse(2, request.queryParams("foo"));
        }
    }
}
