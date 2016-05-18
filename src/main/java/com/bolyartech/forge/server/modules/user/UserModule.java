package com.bolyartech.forge.server.modules.user;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.register.StringEndpointRegister;
import com.bolyartech.forge.server.modules.user.endpoints.UserAutoregistrationEp;
import com.bolyartech.forge.server.modules.user.endpoints.LoginEp;
import com.bolyartech.forge.server.modules.user.endpoints.UserRegistrationEp;
import com.bolyartech.forge.server.modules.user.endpoints.UserRegistrationPostAutoEp;

public class UserModule {
    private final DbPool mDbPool;


    public UserModule(DbPool dbPool, String pathPrefix, StringEndpointRegister stringEndpointRegister) {
        mDbPool = dbPool;

        registerEndpoints(pathPrefix, stringEndpointRegister);
    }


    public void registerEndpoints(String pathPrefix, StringEndpointRegister register) {
        register.register(pathPrefix, new UserAutoregistrationEp(new UserAutoregistrationEp.UserAutoregistrationHandler(mDbPool)));
        register.register(pathPrefix, new UserRegistrationEp(new UserRegistrationEp.RegistrationHandler(mDbPool)));
        register.register(pathPrefix, new LoginEp(new LoginEp.LoginHandler(mDbPool)));
        register.register(pathPrefix, new UserRegistrationPostAutoEp(new UserRegistrationPostAutoEp.UserRegistrationPostAutoHandler(mDbPool)));
    }
}
