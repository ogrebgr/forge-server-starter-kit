package com.bolyartech.forge.server.skeleton;

import com.bolyartech.forge.server.ForgeServerImpl;
import com.bolyartech.forge.server.config.DbConfiguration;
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

import java.io.File;

import static spark.Spark.awaitInitialization;
import static spark.Spark.staticFileLocation;


public class SkelServer extends ForgeServerImpl {
    private DbPool mDbPool;

    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private StringEndpointRegister mStringEndpointRegister;

    private VelocityTemplateEngine mTemplateEngine;


    @Override
    public void start() {
        super.start();

        staticFileLocation("/public_html");
        try {
            mDbPool = createDbPool();
            mTemplateEngine = createVelocityTemplateEngine();

            RootRegister rootRegister = new RootRegisterImpl();
            mStringEndpointRegister = new StringEndpointRegisterImpl(rootRegister, getServerConfiguration().getSessionTimeout());

            initModules();
        } catch (Exception e) {
            mLogger.error("Error: {}", e);
            awaitInitialization();
            stop();
        }
    }


    private void initModules() {
        registerModule(new MainModule(mTemplateEngine, File.separator, mStringEndpointRegister));
        registerModule(new UserModule(mDbPool, File.separator, mStringEndpointRegister));
        registerModule(new AdminModule(mDbPool, File.separator, mStringEndpointRegister));
    }


    public DbPool createDbPool() {
        DbConfiguration conf = ServerTools.loadDbConf(getConfigDirectory(), "db.conf");
        if (conf != null) {
            return ServerTools.createComboPooledDataSource(conf);
        } else {
            return null;
        }
    }


    private VelocityTemplateEngine createVelocityTemplateEngine() {
        return new VelocityTemplateEngine();
    }


    public static void main(String[] args) {
        SkelServer serv = new SkelServer();
        serv.start();
    }
}
