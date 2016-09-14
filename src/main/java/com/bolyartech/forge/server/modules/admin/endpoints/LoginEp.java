package com.bolyartech.forge.server.modules.admin.endpoints;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.StringEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.handlers.db.SecureDbHandler;
import com.bolyartech.forge.server.misc.BasicResponseCodes;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.misc.Params;
import com.bolyartech.forge.server.modules.admin.AdminResponseCodes;
import com.bolyartech.forge.server.modules.admin.data.AdminUser;
import com.bolyartech.forge.server.modules.admin.data.RokLoginAdmin;
import com.bolyartech.forge.server.modules.admin.AdminHandler;
import com.bolyartech.forge.server.modules.admin.data.SessionInfoAdmin;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Session;

import java.sql.Connection;
import java.sql.SQLException;


public class LoginEp extends StringEndpoint {
    public LoginEp(Handler<String> handler) {
        super(HttpMethod.POST, "login", handler);
    }


    public static class AdminLoginHandler extends SecureDbHandler {
        private final Gson mGson;

        public AdminLoginHandler(DbPool dbPool) {
            super(dbPool);
            mGson = new Gson();
        }


        @Override
        public ForgeResponse handleWithDb(Request request, Response response, Connection dbc) throws SQLException {
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            if (Params.areAllPresent(username, password)) {
                AdminUser user = AdminUser.checkLogin(dbc, username, password);
                if (user != null) {
                    Session sess = request.session();
                    sess.attribute(AdminHandler.SESSION_VAR_NAME, user);

                    return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(),
                            mGson.toJson(new RokLoginAdmin(request.session().maxInactiveInterval(),
                                    new SessionInfoAdmin(user.getId(), user.isSuperAdmin()))));
                } else {
                    return new ForgeResponse(AdminResponseCodes.Errors.INVALID_LOGIN.getCode(), "Invalid login");
                }
            } else {
                return new ForgeResponse(BasicResponseCodes.Errors.MISSING_PARAMETERS.getCode(), "Missing parameters");
            }
        }
    }
}
