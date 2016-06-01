package com.bolyartech.forge.server.modules.admin;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.module.AbstractForgeModule;
import com.bolyartech.forge.server.register.StringEndpointRegister;
import com.bolyartech.forge.server.modules.admin.endpoints.*;


public class AdminModule extends AbstractForgeModule {
    private static final String MODULE_SYSTEM_NAME = "admin";
    private static final int MODULE_VERSION_CODE = 1;
    private static final String MODULE_VERSION_NAME = "1.0.0";

    private final DbPool mDbPool;
    private final StringEndpointRegister mRegister;

    public AdminModule(DbPool dbPool,
                       String sitePathPrefix,
                       StringEndpointRegister stringEndpointRegister) {

        this(dbPool, sitePathPrefix, stringEndpointRegister, "api/admin/");
    }


    public AdminModule(DbPool dbPool,
                       String sitePathPrefix,
                       StringEndpointRegister stringEndpointRegister,
                       String modulePathPrefix) {

        super(sitePathPrefix, modulePathPrefix);


        mDbPool = dbPool;
        mRegister = stringEndpointRegister;
    }


    @Override
    public void registerEndpoints() {
        String pathPrefix = getSitePathPrefix() + getModulePathPrefix();

        mRegister.register(pathPrefix, new LoginEp(new LoginEp.AdminLoginHandler(mDbPool)));
        mRegister.register(pathPrefix, new AdminUserListEp(new AdminUserListEp.UserListHandler(mDbPool)));
        mRegister.register(pathPrefix, new CreateUserEp(new CreateUserEp.CreateUserHandler(mDbPool)));
        mRegister.register(pathPrefix, new DisableUserEp(new DisableUserEp.DisableUserHandler(mDbPool)));
        mRegister.register(pathPrefix, new ChangeOwnPasswordEp(new ChangeOwnPasswordEp.ChangeOwnPasswordHandler(mDbPool)));
        mRegister.register(pathPrefix, new ChangePasswordEp(new ChangePasswordEp.ChangeOwnPasswordHandler(mDbPool)));
        mRegister.register(pathPrefix, new LogoutEp(new LogoutEp.LogoutHandler(mDbPool)));
        mRegister.register(pathPrefix, new UserListEp(new UserListEp.UserListHandler(mDbPool)));
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
