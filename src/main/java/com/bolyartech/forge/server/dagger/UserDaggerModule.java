package com.bolyartech.forge.server.dagger;

import com.bolyartech.forge.server.modules.user.data.scram.ScramDbh;
import com.bolyartech.forge.server.modules.user.data.scram.ScramDbhImpl;
import com.bolyartech.forge.server.modules.user.data.user.UserDbh;
import com.bolyartech.forge.server.modules.user.data.user.UserDbhImpl;
import com.bolyartech.forge.server.modules.user.data.user_scram.UserScramDbh;
import com.bolyartech.forge.server.modules.user.data.user_scram.UserScramDbhImpl;
import dagger.Module;
import dagger.Provides;


@Module
public class UserDaggerModule {
    @Provides
    UserScramDbh provideUserScramDbh() {
        return new UserScramDbhImpl();
    }


    @Provides
    UserDbh provideUserDbh() {
        return new UserDbhImpl();
    }


    @Provides
    ScramDbh provideScramDbh() {
        return new ScramDbhImpl();
    }
}
