package com.bolyartech.forge.server.modules.admin.endpoints;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.StringEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.BasicResponseCodes;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.misc.Params;
import com.bolyartech.forge.server.modules.admin.AdminHandler;
import com.bolyartech.forge.server.modules.admin.data.AdminUser;
import com.bolyartech.forge.server.modules.user.data.User;
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
            String userIdRaw = request.queryParams("user");
            String disableRaw = request.queryParams("disable");

            if (Params.areAllPresent(userIdRaw, disableRaw)) {
                try {
                    boolean disable = disableRaw.equals("1");

                    long userId = Long.parseLong(userIdRaw);
                    if (userId == user.getId()) {
                        return new ForgeResponse(ERROR_CANNOT_DISABLE_YOURSELF, "ERROR_CANNOT_DISABLE_YOURSELF");
                    }

                    if (User.disable(dbc, userId, disable)) {
                        return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(),
                                "{disabled: " + (disable ? "true" : "false") + "}");
                    } else {
                        return new ForgeResponse(ERROR_USER_NOT_FOUND, "ERROR_USER_NOT_FOUND");
                    }
                } catch (NumberFormatException e) {
                    return new ForgeResponse(BasicResponseCodes.Errors.INVALID_PARAMETER_VALUE.getCode(), "Invalid id");
                }
            } else {
                return new ForgeResponse(BasicResponseCodes.Errors.MISSING_PARAMETERS.getCode(), "Missing parameters");
            }
        }
    }
}
