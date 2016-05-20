package com.bolyartech.forge.server.modules.main;

import com.bolyartech.forge.server.module.AbstractForgeModule;
import com.bolyartech.forge.server.register.StringEndpointRegister;
import spark.template.velocity.VelocityTemplateEngine;


final public class MainModule extends AbstractForgeModule {
    private static final String MODULE_SYSTEM_NAME = "main";
    private static final int MODULE_VERSION_CODE = 1;
    private static final String MODULE_VERSION_NAME = "1.0.0";

    private final VelocityTemplateEngine mTple;
    private final StringEndpointRegister mRegister;


    public MainModule(VelocityTemplateEngine tple,
                      String sitePathPrefix,
                      StringEndpointRegister register) {

        super(sitePathPrefix, "");
        mTple = tple;
        mRegister = register;
    }


    public MainModule(VelocityTemplateEngine tple,
                      String sitePathPrefix,
                      StringEndpointRegister register,
                      String modulePathPrefix) {

        super(sitePathPrefix, modulePathPrefix);
        mTple = tple;
        mRegister = register;
    }


    @Override
    public void registerEndpoints() {
        String pathPrefix = getSitePathPrefix() + getModulePathPrefix();

        mRegister.register(pathPrefix, new RootEp(new RootEp.RootHandler(mTple)));
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


