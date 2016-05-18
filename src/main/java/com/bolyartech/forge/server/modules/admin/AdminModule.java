package com.bolyartech.forge.server.modules.admin;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.register.StringEndpointRegister;
import com.bolyartech.forge.server.modules.admin.endpoints.*;


public class AdminModule {
    private final DbPool mDbPool;


    public AdminModule(DbPool dbPool, String pathPrefix, StringEndpointRegister stringEndpointRegister) {
        mDbPool = dbPool;
        registerEndpoints(pathPrefix, stringEndpointRegister);
    }


    public void registerEndpoints(String pathPrefix, StringEndpointRegister register) {
        register.register(pathPrefix, new LoginEp(new LoginEp.AdminLoginHandler(mDbPool)));
        register.register(pathPrefix, new UserListEp(new UserListEp.UserListHandler(mDbPool)));
        register.register(pathPrefix, new CreateUserEp(new CreateUserEp.CreateUserHandler(mDbPool)));
        register.register(pathPrefix, new DisableUserEp(new DisableUserEp.DisableUserHandler(mDbPool)));
        register.register(pathPrefix, new ChangeOwnPasswordEp(new ChangeOwnPasswordEp.ChangeOwnPasswordHandler(mDbPool)));
    }
}
