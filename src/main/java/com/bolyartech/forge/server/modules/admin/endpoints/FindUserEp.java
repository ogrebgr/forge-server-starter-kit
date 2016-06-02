package com.bolyartech.forge.server.modules.admin.endpoints;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.StringEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.BasicResponseCodes;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.modules.admin.AdminHandler;
import com.bolyartech.forge.server.modules.admin.data.AdminUser;
import com.bolyartech.forge.server.modules.user.data.User;
import com.bolyartech.forge.server.modules.user.data.UserJson;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FindUserEp extends StringEndpoint {
    public FindUserEp(Handler<String> handler) {
        super(HttpMethod.POST, "user_find", handler);
    }


    public static class FindUserHandler extends AdminHandler {
        private Gson mGson;


        public FindUserHandler(DbPool dbPool) {
            super(dbPool);
            mGson = new Gson();
        }


        @Override
        protected ForgeResponse handleLoggedInAdmin(Request request,
                                                    Response response,
                                                    Connection dbc,
                                                    AdminUser user) throws SQLException {

            String pattern = request.queryParams("pattern").trim();

            if (!Strings.isNullOrEmpty(pattern)) {
                List<UserJson> rez = User.findByPattern(dbc, pattern);
                return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(),
                        mGson.toJson(rez));
            } else {
                return new ForgeResponse(BasicResponseCodes.Errors.MISSING_PARAMETERS.getCode(), "Missing parameters");
            }
        }
    }
}
