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
import java.util.List;
import java.util.stream.Collectors;


public class AdminUserListEp extends StringEndpoint {

    public AdminUserListEp(Handler<String> handler) {
        super(HttpMethod.GET, "admin_users", handler);
    }


    public static class UserListHandler extends AdminHandler {
        private final Gson mGson;

        public UserListHandler(DbPool dbPool) {
            super(dbPool);
            mGson = new Gson();
        }


        @Override
        protected ForgeResponse handleLoggedInAdmin(Request request,
                                                    Response response,
                                                    Connection dbc,
                                                    AdminUser user) throws SQLException {

            List<AdminUser> users = AdminUser.list(dbc);

            List<AdminUserJson> usersJson = users.stream().map(AdminUserJson::new).collect(Collectors.toList());

            return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(),
                    mGson.toJson(usersJson));
        }
    }
}
