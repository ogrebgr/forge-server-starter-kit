package com.bolyartech.forge.server.dagger;

import com.bolyartech.forge.server.config.DbConfiguration;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.ServerTools;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;


@Module
public class DbDaggerModule {
    private final DbConfiguration mDbConfiguration;


    public DbDaggerModule(DbConfiguration dbConfiguration) {
        mDbConfiguration = dbConfiguration;
    }


    @Provides
    @Singleton
    public DbPool provideDbConfiguration() {
        return ServerTools.createComboPooledDataSource(mDbConfiguration);
    }
}
