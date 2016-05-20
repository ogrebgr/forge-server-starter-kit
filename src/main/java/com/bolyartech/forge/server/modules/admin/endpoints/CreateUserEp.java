package com.bolyartech.forge.server.modules.admin.endpoints;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.StringEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.BasicResponseCodes;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.misc.Params;
import com.bolyartech.forge.server.modules.admin.data.AdminUser;
import com.bolyartech.forge.server.modules.user.data.ScreenName;
import com.bolyartech.forge.server.modules.user.data.User;
import com.bolyartech.forge.server.modules.admin.AdminHandler;
import com.bolyartech.forge.server.modules.user.UserResponseCodes;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateUserEp extends StringEndpoint {
    public CreateUserEp(Handler<String> handler) {
        super(HttpMethod.POST, "create_user", handler);
    }


    public static class CreateUserHandler extends AdminHandler {

        public CreateUserHandler(DbPool dbPool) {
            super(dbPool);
        }


        @Override
        protected ForgeResponse handleLoggedInAdmin(Request request, Response response, Connection dbc, AdminUser user) throws SQLException {
            if (user.isSuperAdmin()) {
                String username = request.queryParams("username").trim();
                String password = request.queryParams("password");
                String name = request.queryParams("name".trim());
                String superAdminRaw = request.queryParams("superadmin".trim());

                if (Params.areAllPresent(username, password, name)) {
                    if (!User.isValidUsername(username)) {
                        return new ForgeResponse(UserResponseCodes.Errors.INVALID_USERNAME.getCode(), "Invalid username");
                    }

                    if (!ScreenName.isValid(name)) {
                        return new ForgeResponse(UserResponseCodes.Errors.INVALID_SCREEN_NAME.getCode(), "Invalid screen name");
                    }

                    if (!AdminUser.isValidPasswordLenght(password)) {
                        return new ForgeResponse(UserResponseCodes.Errors.PASSWORD_TOO_SHORT.getCode(), "Invalid screen name");
                    }

                    boolean superAdmin;
                    if (superAdminRaw != null) {
                        superAdmin = superAdminRaw.equals("1");
                    } else {
                        superAdmin = false;
                    }


                    try {
                        Statement lockSt = dbc.createStatement();
                        lockSt.execute("LOCK TABLES admin_users WRITE");

                        if (AdminUser.usernameExists(dbc, username)) {
                            return new ForgeResponse(UserResponseCodes.Errors.USERNAME_EXISTS.getCode(), "username taken");
                        }

                        AdminUser.createNew(dbc, username, password, false, superAdmin, name);

                        return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(), "OK");
                    } finally {
                        Statement unlockSt = dbc.createStatement();
                        unlockSt.execute("UNLOCK TABLES");
                    }
                } else {
                    return new ForgeResponse(BasicResponseCodes.Errors.MISSING_PARAMETERS.getCode(), "Missing parameters");
                }
            } else {
                return new ForgeResponse(UserResponseCodes.Errors.NO_ENOUGH_PRIVILEGES.getCode(), "Missing parameters");
            }
        }
    }
}
