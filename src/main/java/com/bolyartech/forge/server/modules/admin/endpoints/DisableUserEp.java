package com.bolyartech.forge.server.modules.admin.endpoints;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.StringEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.BasicResponseCodes;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.modules.admin.data.AdminUser;
import com.bolyartech.forge.server.modules.admin.AdminHandler;
import com.bolyartech.forge.server.modules.user.UserResponseCodes;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;

public class DisableUserEp extends StringEndpoint {
    private static final int ERROR_USER_NOT_FOUND = -100;
    private static final int ERROR_CANNOT_DISABLE_YOURSELF = -101;

    public DisableUserEp(Handler<String> handler) {
        super(HttpMethod.POST, "user_disable", handler);
    }


    public static class DisableUserHandler extends AdminHandler {
        public DisableUserHandler(DbPool dbPool) {
            super(dbPool);
        }


        @Override
        protected ForgeResponse handleLoggedInAdmin(Request request, Response response, Connection dbc, AdminUser user) throws SQLException {
            if (user.isSuperAdmin()) {
                String userIdRaw = request.queryParams("user").trim();
                try {
                    long userId = Long.parseLong(userIdRaw);
                    if (userId == user.getId()) {
                        return new ForgeResponse(ERROR_CANNOT_DISABLE_YOURSELF, "ERROR_CANNOT_DISABLE_YOURSELF");
                    }

                    if (AdminUser.disable(dbc, userId)) {
                        return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(), "DISABLED");
                    } else {
                        return new ForgeResponse(ERROR_USER_NOT_FOUND, "ERROR_USER_NOT_FOUND");
                    }
                } catch (NumberFormatException e) {
                    return new ForgeResponse(BasicResponseCodes.Errors.INVALID_PARAMETER_VALUE.getCode(), "Invalid id");
                }
            } else {
                return new ForgeResponse(UserResponseCodes.Errors.NO_ENOUGH_PRIVILEGES.getCode(), "Missing parameters");
            }
        }
    }
}
