package com.bolyartech.forge.server.modules.main;

import com.bolyartech.forge.server.module.AbstractForgeModule;
import com.bolyartech.forge.server.register.StringEndpointRegister;
import spark.TemplateEngine;
import spark.template.velocity.VelocityTemplateEngine;

import javax.inject.Inject;
import javax.inject.Named;


final public class MainModule extends AbstractForgeModule {
    private static final String MODULE_SYSTEM_NAME = "main";
    private static final int MODULE_VERSION_CODE = 1;
    private static final String MODULE_VERSION_NAME = "1.0.0";

    private final TemplateEngine mTple;
    private final StringEndpointRegister mRegister;


    @Inject
    public MainModule(TemplateEngine tple,
                      StringEndpointRegister register) {

        super("/");
        mTple = tple;
        mRegister = register;
    }


    public MainModule(VelocityTemplateEngine tple,
                      StringEndpointRegister register,
                      String modulePathPrefix) {

        super(modulePathPrefix);
        mTple = tple;
        mRegister = register;
    }


    @Override
    public void registerEndpoints() {
        String pathPrefix = getModulePathPrefix();

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


