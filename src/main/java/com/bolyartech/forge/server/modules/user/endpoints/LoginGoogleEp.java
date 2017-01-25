package com.bolyartech.forge.server.modules.user.endpoints;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.handler.ForgeDbSecureEndpoint;
import com.bolyartech.forge.server.misc.Params;
import com.bolyartech.forge.server.modules.user.ExternalUser;
import com.bolyartech.forge.server.modules.user.LoginType;
import com.bolyartech.forge.server.modules.user.SessionVars;
import com.bolyartech.forge.server.modules.user.UserResponseCodes;
import com.bolyartech.forge.server.modules.user.data.SessionInfo;
import com.bolyartech.forge.server.modules.user.data.screen_name.ScreenName;
import com.bolyartech.forge.server.modules.user.data.screen_name.ScreenNameDbh;
import com.bolyartech.forge.server.modules.user.data.user.User;
import com.bolyartech.forge.server.modules.user.data.user.UserDbh;
import com.bolyartech.forge.server.modules.user.data.user_ext_id.UserExtId;
import com.bolyartech.forge.server.modules.user.data.user_ext_id.UserExtIdDbh;
import com.bolyartech.forge.server.modules.user.google.GoogleSignInWrapper;
import com.bolyartech.forge.server.response.ResponseException;
import com.bolyartech.forge.server.response.forge.ForgeResponse;
import com.bolyartech.forge.server.response.forge.MissingParametersResponse;
import com.bolyartech.forge.server.response.forge.OkResponse;
import com.bolyartech.forge.server.route.RequestContext;
import com.bolyartech.forge.server.session.Session;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.sql.Connection;
import java.sql.SQLException;


public class LoginGoogleEp extends ForgeDbSecureEndpoint {
    public static final String PARAM_TOKEN = "token";

    private final UserDbh mUserDbh;
    private final UserExtIdDbh mUserExtIdDbh;
    private final ScreenNameDbh mScreenNameDbh;
    private final GoogleSignInWrapper mGoogleSignInWrapper;

    private final Gson mGson;


    public LoginGoogleEp(DbPool dbPool, UserDbh userDbh, UserExtIdDbh userExtIdDbh,
                         ScreenNameDbh screenNameDbh, GoogleSignInWrapper googleSignInWrapper) {

        super(dbPool);
        mUserDbh = userDbh;
        mUserExtIdDbh = userExtIdDbh;
        mScreenNameDbh = screenNameDbh;
        mGoogleSignInWrapper = googleSignInWrapper;

        mGson = new Gson();
    }


    @Override
    public ForgeResponse handleForgeSecure(RequestContext ctx, Connection dbc) throws ResponseException, SQLException {

        String token = ctx.getFromPost(PARAM_TOKEN);

        if (Params.areAllPresent(token)) {
            ExternalUser googleUser = mGoogleSignInWrapper.checkToken(token);
            if (googleUser != null) {
                Session session = ctx.getSession();
                User user = session.getVar(SessionVars.VAR_USER);
                if (user != null) {
                    UserExtId userExtId = mUserExtIdDbh.loadByUser(dbc, user.getId(), UserExtId.Type.GOOGLE);
                    if (userExtId == null) {
                        // first login using fb
                        return processLoggedFirstGoogleLogin(ctx, dbc, user, googleUser);
                    } else {
                        // returning fb user
                        return processLoggedReturningGoogleUser(ctx, userExtId, dbc, user, googleUser);
                    }
                } else {
                    return processNotLogged(ctx, dbc, googleUser);
                }
            } else {
                return new ForgeResponse(UserResponseCodes.Errors.INVALID_LOGIN, "Invalid google login");
            }
        } else {
            return MissingParametersResponse.getInstance();
        }
    }


    private SessionInfo createSessionInfo(Connection dbc, long userId) throws SQLException {
        ScreenName sn = mScreenNameDbh.loadByUser(dbc, userId);

        SessionInfo si;
        if (sn != null) {
            si = new SessionInfo(userId, sn.getScreenName());
        } else {
            si = new SessionInfo(userId, null);
        }

        return si;
    }


    private ForgeResponse completeLogin(RequestContext ctx, Connection dbc, User user) throws SQLException {
        Session session = ctx.getSession();
        session.setVar(SessionVars.VAR_USER, user);
        session.setVar(SessionVars.VAR_LOGIN_TYPE, LoginType.GOOGLE);

        SessionInfo si = createSessionInfo(dbc, user.getId());

        return new OkResponse(mGson.toJson(new RokLogin(session.getMaxInactiveInterval(), si)));
    }


    private ForgeResponse processNotLogged(RequestContext ctx, Connection dbc, ExternalUser fbUser)
            throws SQLException {

        UserExtId extIdByFbId = mUserExtIdDbh.loadByExtId(dbc, fbUser.getExternalId(), UserExtId.Type.GOOGLE);
        if (extIdByFbId != null) {
            User user = mUserDbh.loadById(dbc, extIdByFbId.getUser());
            return completeLogin(ctx, dbc, user);
        } else {
            return new ForgeResponse(UserResponseCodes.Errors.INVALID_LOGIN, "Invalid google login");
        }
    }


    private ForgeResponse processLoggedReturningGoogleUser(RequestContext ctx, UserExtId userExtId, Connection dbc,
                                                           User user, ExternalUser fbUser) throws SQLException {

        UserExtId extIdByFbId = mUserExtIdDbh.loadByExtId(dbc, fbUser.getExternalId(), UserExtId.Type.GOOGLE);
        if (extIdByFbId.getId() == userExtId.getId()) {
            return completeLogin(ctx, dbc, user);
        } else {
            // attempt to use multiple fb accounts
            return new ForgeResponse(UserResponseCodes.Errors.INVALID_LOGIN, "Invalid google login");
        }
    }


    private ForgeResponse processLoggedFirstGoogleLogin(RequestContext ctx, Connection dbc, User user,
                                                        ExternalUser fbUser) throws SQLException {


        mUserExtIdDbh.createNew(dbc, user.getId(), fbUser.getExternalId(), UserExtId.Type.GOOGLE);
        return completeLogin(ctx, dbc, user);
    }

    public static class RokLogin {
        @SerializedName("session_ttl")
        public final int sessionTtl;
        @SerializedName("session_info")
        public final SessionInfo sessionInfo;


        public RokLogin(int sessionTtl, SessionInfo sessionInfo) {
            this.sessionTtl = sessionTtl;
            this.sessionInfo = sessionInfo;
        }
    }
}
