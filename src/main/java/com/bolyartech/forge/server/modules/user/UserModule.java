package com.bolyartech.forge.server.modules.user;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.module.AbstractForgeModule;
import com.bolyartech.forge.server.modules.user.endpoints.*;
import com.bolyartech.forge.server.register.StringEndpointRegister;

public class UserModule extends AbstractForgeModule {
    private static final String MODULE_SYSTEM_NAME = "user";
    private static final int MODULE_VERSION_CODE = 1;
    private static final String MODULE_VERSION_NAME = "1.0.0";

    private final DbPool mDbPool;
    private final StringEndpointRegister mRegister;

    public UserModule(DbPool dbPool,
                      String sitePathPrefix,
                      StringEndpointRegister stringEndpointRegister) {

        this(dbPool, sitePathPrefix, stringEndpointRegister, "api/user/");
    }


    public UserModule(DbPool dbPool,
                      String sitePathPrefix,
                      StringEndpointRegister stringEndpointRegister,
                      String modulePathPrefix
                      ) {
        super(sitePathPrefix, modulePathPrefix);

        mDbPool = dbPool;
        mRegister = stringEndpointRegister;
    }


    @Override
    public void registerEndpoints() {
        String pathPrefix = getSitePathPrefix() + getModulePathPrefix();

        mRegister.register(pathPrefix,
                new AutoregistrationEp(new AutoregistrationEp.UserAutoregistrationHandler(mDbPool)));

        mRegister.register(pathPrefix,
                new RegistrationEp(new RegistrationEp.RegistrationHandler(mDbPool)));

        mRegister.register(pathPrefix, new LoginEp(new LoginEp.LoginHandler(mDbPool)));

        mRegister.register(pathPrefix,
                new RegistrationPostAutoEp(new RegistrationPostAutoEp.UserRegistrationPostAutoHandler(mDbPool)));

        mRegister.register(pathPrefix,
                new ScreenNameEp(new ScreenNameEp.ScreenNameHandler(mDbPool)));
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
