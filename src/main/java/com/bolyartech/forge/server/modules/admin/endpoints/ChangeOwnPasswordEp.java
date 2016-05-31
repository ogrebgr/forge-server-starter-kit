package com.bolyartech.forge.server.modules.admin.endpoints;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.StringEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.BasicResponseCodes;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.modules.admin.AdminResponseCodes;
import com.bolyartech.forge.server.modules.admin.data.AdminUser;
import com.bolyartech.forge.server.modules.admin.AdminHandler;
import com.google.common.base.Strings;
import spark.Request;
import spark.Response;

import java.sql.Connection;
import java.sql.SQLException;


public class ChangeOwnPasswordEp extends StringEndpoint {
    public ChangeOwnPasswordEp(Handler<String> handler) {
        super(HttpMethod.POST, "change_own_password", handler);
    }


    public static class ChangeOwnPasswordHandler extends AdminHandler {

        public ChangeOwnPasswordHandler(DbPool dbPool) {
            super(dbPool);
        }


        @Override
        protected ForgeResponse handleLoggedInAdmin(Request request, Response response, Connection dbc, AdminUser user) throws SQLException {
            String newPassword = request.queryParams("new_password");

            if (Strings.isNullOrEmpty(newPassword)) {
                return new ForgeResponse(BasicResponseCodes.Errors.MISSING_PARAMETERS.getCode(), "Missing parameters");
            }

            if (!AdminUser.isValidPasswordLength(newPassword)) {
                return new ForgeResponse(AdminResponseCodes.Errors.PASSWORD_TOO_SHORT.getCode(), "Invalid screen name");
            }

            AdminUser.changePassword(dbc, user.getId(), newPassword);

            return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(), "Password changed");
        }
    }

}
