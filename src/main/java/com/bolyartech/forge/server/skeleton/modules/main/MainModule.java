package com.bolyartech.forge.server.skeleton.modules.main;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.register.MavEndpointRegister;

public class MainModule {
    private final DbPool mDbPool;


    public MainModule(DbPool dbPool) {
        mDbPool = dbPool;
    }


    public void registerEndpoints(MavEndpointRegister register) {
        register.register(new RootEp(new RootEp.RootHandler()));
    }
}
