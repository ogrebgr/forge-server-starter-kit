package com.bolyartech.forge.server.modules.user;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.module.AbstractForgeModule;
import com.bolyartech.forge.server.modules.user.data.scram.ScramDbh;
import com.bolyartech.forge.server.modules.user.data.user.UserDbh;
import com.bolyartech.forge.server.modules.user.data.user_scram.UserScramDbh;
import com.bolyartech.forge.server.modules.user.endpoints.*;
import com.bolyartech.forge.server.register.StringEndpointRegister;

import javax.inject.Inject;


public class UserModule extends AbstractForgeModule {
    private static final String MODULE_SYSTEM_NAME = "user";
    private static final int MODULE_VERSION_CODE = 1;
    private static final String MODULE_VERSION_NAME = "1.0.0";

    private final DbPool mDbPool;
    private final StringEndpointRegister mRegister;
    private final UserScramDbh mUserScramDbh;
    private final UserDbh mUserDbh;
    private final ScramDbh mScramDbh;


    @Inject
    public UserModule(DbPool dbPool,
                      StringEndpointRegister stringEndpointRegister,
                      UserScramDbh userScramDbh,
                      UserDbh userDbh,
                      ScramDbh scramDbh) {

        super("/api/user/");

        mDbPool = dbPool;
        mRegister = stringEndpointRegister;

        mUserScramDbh = userScramDbh;
        mUserDbh = userDbh;
        mScramDbh = scramDbh;
    }


    @Override
    public void registerEndpoints() {
        String pathPrefix = getModulePathPrefix();

        mRegister.register(pathPrefix,
                new AutoregistrationEp(new AutoregistrationEp.UserAutoregistrationHandler(mDbPool,
                        mUserScramDbh,
                        mUserDbh,
                        mScramDbh)));

//        mRegister.register(pathPrefix,
//                new RegistrationEp(new RegistrationEp.RegistrationHandler(mDbPool)));
//
//        mRegister.register(pathPrefix, new LoginEp(new LoginEp.LoginHandler(mDbPool)));
//
//        mRegister.register(pathPrefix,
//                new RegistrationPostAutoEp(new RegistrationPostAutoEp.UserRegistrationPostAutoHandler(mDbPool)));
//
//        mRegister.register(pathPrefix,
//                new ScreenNameEp(new ScreenNameEp.ScreenNameHandler(mDbPool)));
//
//        mRegister.register(pathPrefix,
//                new LogoutEp(new LogoutEp.LogoutHandler(mDbPool)));
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
