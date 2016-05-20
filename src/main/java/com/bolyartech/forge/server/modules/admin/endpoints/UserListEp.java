package com.bolyartech.forge.server.modules.admin.endpoints;


import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.StringEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.BasicResponseCodes;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.modules.admin.data.AdminUser;
import com.bolyartech.forge.server.modules.admin.data.AdminUserJson;
import com.bolyartech.forge.server.modules.admin.AdminHandler;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UserListEp extends StringEndpoint {

    public UserListEp(Handler<String> handler) {
        super(HttpMethod.GET, "users", handler);
    }


    public static class UserListHandler extends AdminHandler {
        private Gson mGson;

        public UserListHandler(DbPool dbPool) {
            super(dbPool);
            mGson = new Gson();
        }


        @Override
        protected ForgeResponse handleLoggedInAdmin(Request request, Response response, Connection dbc, AdminUser user) throws SQLException {
            List<AdminUser> users = AdminUser.list(dbc);

            List<AdminUserJson> usersJson = new ArrayList<>();

            for (AdminUser u : users) {
                usersJson.add(new AdminUserJson(u));
            }

            return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(),
                    mGson.toJson(usersJson));
        }
    }
}
