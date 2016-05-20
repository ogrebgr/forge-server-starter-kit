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
import com.bolyartech.forge.server.modules.user.UserResponseCodes;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;

public class ChangePasswordEp extends StringEndpoint {

    public ChangePasswordEp(Handler<String> handler) {
        super(HttpMethod.POST, "change_password", handler);
    }

    public static class ChangeOwnPasswordHandler extends AdminHandler {
        public ChangeOwnPasswordHandler(DbPool dbPool) {
            super(dbPool);
        }


        @Override
        protected ForgeResponse handleLoggedInAdmin(Request request,
                                                    Response response,
                                                    Connection dbc,
                                                    AdminUser user) throws SQLException {

            if (user.isSuperAdmin()) {
                String userIdRaw = request.queryParams("user").trim();
                String newPassword = request.queryParams("new_password");

                if (Params.areAllPresent(userIdRaw, newPassword)) {
                    if (!AdminUser.isValidPasswordLenght(newPassword)) {
                        return new ForgeResponse(UserResponseCodes.Errors.PASSWORD_TOO_SHORT.getCode(), "Invalid screen name");
                    }

                    try {
                        long userId = Long.parseLong(userIdRaw);
                        AdminUser.changePassword(dbc, userId, newPassword);
                    } catch (NumberFormatException e) {
                        return new ForgeResponse(BasicResponseCodes.Errors.INVALID_PARAMETER_VALUE.getCode(), "Invalid id");
                    }

                    return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(), "Password changed");
                } else {
                    return new ForgeResponse(BasicResponseCodes.Errors.MISSING_PARAMETERS.getCode(), "Missing parameters");
                }
            } else {
                return new ForgeResponse(UserResponseCodes.Errors.NO_ENOUGH_PRIVILEGES.getCode(), "Missing parameters");
            }
        }
    }
}
