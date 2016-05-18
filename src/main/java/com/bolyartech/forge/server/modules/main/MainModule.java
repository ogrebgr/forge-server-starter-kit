package com.bolyartech.forge.server.modules.main;

import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.register.StringEndpointRegister;
import spark.template.velocity.VelocityTemplateEngine;

final public class MainModule {
    private final VelocityTemplateEngine mTple;


    public MainModule(VelocityTemplateEngine tple, String pathPrefix, StringEndpointRegister register) {
        mTple = tple;

        registerEndpoints(pathPrefix, register);
    }


    public void registerEndpoints(String pathPrefix, StringEndpointRegister register) {
        register.register(pathPrefix, new RootEp(new RootEp.RootHandler(mTple)));
    }
}
