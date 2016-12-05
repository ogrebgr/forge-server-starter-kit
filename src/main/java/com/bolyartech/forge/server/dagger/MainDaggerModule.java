package com.bolyartech.forge.server.dagger;

import com.bolyartech.forge.server.register.RootRegister;
import com.bolyartech.forge.server.register.RootRegisterImpl;
import com.bolyartech.forge.server.register.StringEndpointRegister;
import com.bolyartech.forge.server.register.StringEndpointRegisterImpl;
import dagger.Module;
import dagger.Provides;
import spark.TemplateEngine;
import spark.template.velocity.VelocityTemplateEngine;

import javax.inject.Named;
import javax.inject.Singleton;


@Module
public class MainDaggerModule {
    private final String mSitePathPrefix;
    private final int mSessionTimeoutSeconds;


    public MainDaggerModule(String sitePathPrefix, int sessionTimeoutSeconds) {
        mSitePathPrefix = sitePathPrefix;
        mSessionTimeoutSeconds = sessionTimeoutSeconds;
    }


    @Provides
    @Singleton
    TemplateEngine provideTemplateEngine() {
        return new VelocityTemplateEngine();
    }


    @Provides
    @Named("site path prefix")
    String provideSitePathPrefix() {
        return mSitePathPrefix;
    }


    @Provides
    StringEndpointRegister provideStringEndpointRegister() {
        RootRegister rootRegister = new RootRegisterImpl();
        return new StringEndpointRegisterImpl(rootRegister, mSessionTimeoutSeconds);
    }
}
