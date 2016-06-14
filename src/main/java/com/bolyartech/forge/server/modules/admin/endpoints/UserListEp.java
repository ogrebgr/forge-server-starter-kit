package com.bolyartech.forge.server.modules.admin.endpoints;


import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.StringEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.BasicResponseCodes;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.modules.admin.AdminHandler;
import com.bolyartech.forge.server.modules.admin.data.AdminUser;
import com.bolyartech.forge.server.modules.user.data.UserJson;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class UserListEp extends StringEndpoint {
    private static final int USERS_PAGE_SIZE = 10;

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
        protected ForgeResponse handleLoggedInAdmin(Request request,
                                                    Response response,
                                                    Connection dbc,
                                                    AdminUser user) throws SQLException {

            String idGreaterThanRaw = request.queryParams("id");
            if (Strings.isNullOrEmpty(idGreaterThanRaw)) {
                long id = 0;
                if (!Strings.isNullOrEmpty(idGreaterThanRaw)) {
                    try {
                        id = Long.parseLong(idGreaterThanRaw);
                    } catch (NumberFormatException e) {
                        return new ForgeResponse(BasicResponseCodes.Errors.INVALID_PARAMETER_VALUE.getCode(), "Invalid id: " + id);
                    }

                }

                List<UserJson> users = UserJson.list(dbc, id, USERS_PAGE_SIZE);

                return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(), mGson.toJson(users));
            } else {
                return new ForgeResponse(BasicResponseCodes.Errors.MISSING_PARAMETERS.getCode(), "Missing parameters");
            }
        }
    }
}
