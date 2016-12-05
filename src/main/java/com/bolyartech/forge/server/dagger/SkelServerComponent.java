package com.bolyartech.forge.server.dagger;

import com.bolyartech.forge.server.SkelServer;
import dagger.Component;

import javax.inject.Singleton;


@Component(modules = {MainDaggerModule.class, UserDaggerModule.class})
@Singleton
public interface SkelServerComponent {
    void inject(SkelServer srv);
}
