package com.bolyartech.forge.server.skeleton.modules.api;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.register.MavEndpointRegister;
import com.bolyartech.forge.server.skeleton.modules.main.RootEp;

public class ApiModule {
    private final DbPool mDbPool;


    public ApiModule(DbPool dbPool) {
        mDbPool = dbPool;
    }


    public void registerEndpoints(MavEndpointRegister register) {
        register.register(new UserLoginEp(new UserLoginEp.UserLoginHandler(mDbPool)));
    }
}
