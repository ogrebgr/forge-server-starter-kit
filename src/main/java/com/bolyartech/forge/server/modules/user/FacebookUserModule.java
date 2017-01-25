package com.bolyartech.forge.server.modules.user;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.module.HttpModule;
import com.bolyartech.forge.server.modules.user.data.scram.ScramDbh;
import com.bolyartech.forge.server.modules.user.data.screen_name.ScreenNameDbh;
import com.bolyartech.forge.server.modules.user.data.user.UserDbh;
import com.bolyartech.forge.server.modules.user.data.user_ext_id.UserExtIdDbh;
import com.bolyartech.forge.server.modules.user.endpoints.LoginFacebookEp;
import com.bolyartech.forge.server.modules.user.facebook.FacebookWrapper;
import com.bolyartech.forge.server.route.PostRoute;
import com.bolyartech.forge.server.route.Route;

import java.util.ArrayList;
import java.util.List;


public class FacebookUserModule implements HttpModule {
    private static final String DEFAULT_PATH_PREFIX = "/api/user/";

    private static final String MODULE_SYSTEM_NAME = "user_facebook";
    private static final int MODULE_VERSION_CODE = 1;
    private static final String MODULE_VERSION_NAME = "1.0.0";

    private final String mPathPrefix;
    private final DbPool mDbPool;
    private final UserDbh mUserDbh;
    private final ScramDbh mScramDbh;
    private final ScreenNameDbh mScreenNameDbh;
    private final UserExtIdDbh mUserExtIdDbh;
    private final FacebookWrapper mFacebookWrapper;


    public FacebookUserModule(String pathPrefix, DbPool dbPool, UserDbh userDbh, ScramDbh scramDbh,
                              ScreenNameDbh screenNameDbh, UserExtIdDbh userExtIdDbh, FacebookWrapper facebookWrapper) {

        mPathPrefix = pathPrefix;
        mDbPool = dbPool;
        mUserDbh = userDbh;
        mScramDbh = scramDbh;
        mScreenNameDbh = screenNameDbh;
        mUserExtIdDbh = userExtIdDbh;
        mFacebookWrapper = facebookWrapper;
    }


    public FacebookUserModule(DbPool dbPool, UserDbh userDbh, ScramDbh scramDbh, ScreenNameDbh screenNameDbh,
                              UserExtIdDbh userExtIdDbh, FacebookWrapper facebookWrapper) {

        this(DEFAULT_PATH_PREFIX, dbPool, userDbh, scramDbh, screenNameDbh, userExtIdDbh, facebookWrapper);
    }


    @Override
    public List<Route> createRoutes() {
        List<Route> ret = new ArrayList<>();

        ret.add(new PostRoute(mPathPrefix + "login_facebook",
                new LoginFacebookEp(mDbPool, mUserDbh, mUserExtIdDbh, mScreenNameDbh, mFacebookWrapper)));

        return ret;
    }


    @Override
    public String getSystemName() {
        return MODULE_SYSTEM_NAME;
    }


    @Override
    public String getShortDescription() {
        return "";
    }


    @Override
    public int getVersionCode() {
        return MODULE_VERSION_CODE;
    }


    @Override
    public String getVersionName() {
        return MODULE_VERSION_NAME;
    }
}
