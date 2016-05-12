package com.bolyartech.forge.server.skeleton.modules.api;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.register.MavEndpointRegister;
import com.bolyartech.forge.server.skeleton.modules.api.autoreg.UserAutoregistrationEp;
import com.bolyartech.forge.server.skeleton.modules.api.login.LoginEp;
import com.bolyartech.forge.server.skeleton.modules.api.register.UserRegistrationEp;
import com.bolyartech.forge.server.skeleton.modules.api.register_postauto.UserRegistrationPostAutoEp;

public class ApiModule {
    private final DbPool mDbPool;


    public ApiModule(DbPool dbPool) {
        mDbPool = dbPool;
    }


    public void registerEndpoints(MavEndpointRegister register) {
        register.register(new UserAutoregistrationEp(new UserAutoregistrationEp.UserAutoregistrationHandler(mDbPool)));
        register.register(new UserRegistrationEp(new UserRegistrationEp.RegistrationHandler(mDbPool)));
        register.register(new LoginEp(new LoginEp.LoginHandler(mDbPool)));
        register.register(new UserRegistrationPostAutoEp(new UserRegistrationPostAutoEp.UserRegistrationPostAutoHandler(mDbPool)));
    }
}
