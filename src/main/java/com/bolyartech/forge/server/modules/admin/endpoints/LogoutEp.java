package com.bolyartech.forge.server.modules.admin.endpoints;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.StringEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.handlers.db.SecureDbHandler;
import com.bolyartech.forge.server.misc.BasicResponseCodes;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.modules.admin.AdminHandler;
import spark.Request;
import spark.Response;
import spark.Session;

import java.sql.Connection;
import java.sql.SQLException;

public class LogoutEp extends StringEndpoint {
    public LogoutEp(Handler<String> handler) {
        super(HttpMethod.GET, "logout", handler);
    }

    public static class LogoutHandler extends SecureDbHandler {
        public LogoutHandler(DbPool dbPool) {
            super(dbPool);
        }


        @Override
        public ForgeResponse handleWithDb(Request request, Response response, Connection dbc) throws SQLException {
            Session sess = request.session();
            sess.attribute(AdminHandler.SESSION_VAR_NAME, null);

            return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(), "logged out");
        }
    }
}
