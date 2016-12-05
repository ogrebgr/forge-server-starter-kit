package com.bolyartech.forge.server;

import com.bolyartech.forge.server.config.DbConfiguration;
import com.bolyartech.forge.server.dagger.DaggerSkelServerComponent;
import com.bolyartech.forge.server.dagger.MainDaggerModule;
import com.bolyartech.forge.server.dagger.SkelServerComponent;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.ServerTools;
import com.bolyartech.forge.server.modules.admin.AdminModule;
import com.bolyartech.forge.server.modules.main.MainModule;
import com.bolyartech.forge.server.modules.user.UserModule;
import com.bolyartech.forge.server.register.RootRegister;
import com.bolyartech.forge.server.register.RootRegisterImpl;
import com.bolyartech.forge.server.register.StringEndpointRegister;
import com.bolyartech.forge.server.register.StringEndpointRegisterImpl;
import org.slf4j.LoggerFactory;
import spark.template.velocity.VelocityTemplateEngine;

import javax.inject.Inject;
import java.io.File;

import static spark.Spark.awaitInitialization;
import static spark.Spark.staticFileLocation;


public class SkelServer extends ForgeServerImpl {
    private DbPool mDbPool;

    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Inject
    MainModule mMainModule;

    @Override
    public void start() {
        super.start();

        SkelServerComponent injector = createInjector();
        injector.inject(this);


        staticFileLocation("/public_html");
        try {
            mDbPool = createDbPool();

            initModules();
        } catch (Exception e) {
            mLogger.error("Error: {}", e);
            awaitInitialization();
            stop();
        }
    }


    private void initModules() {
        registerModule(mMainModule);
//        registerModule(new UserModule(mDbPool, File.separator, mStringEndpointRegister));
//        registerModule(new AdminModule(mDbPool, File.separator, mStringEndpointRegister));
    }


    public DbPool createDbPool() {
        DbConfiguration conf = ServerTools.loadDbConf(getConfigDirectory(), "db.conf");
        if (conf != null) {
            return ServerTools.createComboPooledDataSource(conf);
        } else {
            return null;
        }
    }


    public static void main(String[] args) {

        SkelServer serv = new SkelServer();
        serv.start();
    }


    private SkelServerComponent createInjector() {
        return DaggerSkelServerComponent.builder().mainDaggerModule(new MainDaggerModule("/",
                getServerConfiguration().getSessionTimeout())).build();
    }
}
