package com.bolyartech.forge.server.modules.swoon;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.module.AbstractForgeModule;
import com.bolyartech.forge.server.register.StringEndpointRegister;

public class SwoonModule extends AbstractForgeModule {
    private static final String MODULE_SYSTEM_NAME = "swoon";
    private static final int MODULE_VERSION_CODE = 1;
    private static final String MODULE_VERSION_NAME = "1.0.0";

    private final DbPool mDbPool;
    private final StringEndpointRegister mRegister;


    public SwoonModule(DbPool dbPool,
                       String sitePathPrefix,
                       StringEndpointRegister stringEndpointRegister) {
        this(dbPool, sitePathPrefix, stringEndpointRegister, "api/user/");
    }


    public SwoonModule(DbPool dbPool,
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
