package com.bolyartech.forge.server.modules.user.endpoints;

import com.bolyartech.forge.server.Handler;
import com.bolyartech.forge.server.HttpMethod;
import com.bolyartech.forge.server.StringEndpoint;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.handlers.db.SecureDbHandler;
import com.bolyartech.forge.server.misc.BasicResponseCodes;
import com.bolyartech.forge.server.misc.ForgeResponse;
import com.bolyartech.forge.server.modules.user.UserHandler;
import com.bolyartech.forge.server.modules.user.data.RokResponseAutoregistration;
import com.bolyartech.forge.server.modules.user.data.SessionInfo;
import com.bolyartech.forge.server.modules.user.data.scram.ScramDbh;
import com.bolyartech.forge.server.modules.user.data.user.UserDbh;
import com.bolyartech.forge.server.modules.user.data.user_scram.UserScram;
import com.bolyartech.forge.server.modules.user.data.user_scram.UserScramDbh;
import com.google.gson.Gson;
import com.bolyartech.scram_sasl.common.ScramUtils;
import spark.Request;
import spark.Response;
import spark.Session;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;


public class AutoregistrationEp extends StringEndpoint {
    public AutoregistrationEp(Handler<String> handler) {
        super(HttpMethod.POST, "autoregister", handler);
    }


    public static class UserAutoregistrationHandler extends SecureDbHandler {
        private final Gson mGson;
        private final UserScramDbh mUserScramDbh;
        private final UserDbh mUserDbh;
        private final ScramDbh mScramDbh;

        public UserAutoregistrationHandler(DbPool dbPool, UserScramDbh userScramDbh,
                                           UserDbh userDbh, ScramDbh scramDbh) {

            super(dbPool);
            mGson = new Gson();
            mUserScramDbh = userScramDbh;
            mUserDbh = userDbh;
            mScramDbh = scramDbh;
        }


        @Override
        public ForgeResponse handleWithDb(Request request, Response response, Connection dbc) throws SQLException {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[24];
            random.nextBytes(salt);

            String username;
            String password = UUID.randomUUID().toString();
            UserScram us;

            while(true) {
                username = UUID.randomUUID().toString();

                try {
                    ScramUtils.NewPasswordStringData data = ScramUtils.byteArrayToStringData(
                            ScramUtils.newPassword(password, salt, 4096, "HmacSHA512", "SHA-512")
                    );

                    us = mUserScramDbh.generateUser(dbc, mUserDbh, mScramDbh, username, data);
                    if (us != null) {
                        break;
                    }
                } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                    throw new RuntimeException(e);
                }
            }

            SessionInfo si = new SessionInfo(us.getUser().getId(), null);

            Session sess = request.session();
            sess.attribute(UserHandler.SESSION_VAR_NAME, us.getUser());

            return new ForgeResponse(BasicResponseCodes.Oks.OK.getCode(),
                    mGson.toJson(new RokResponseAutoregistration(username,
                            password,
                            sess.maxInactiveInterval(),
                            si
                    )));
        }
    }

}
