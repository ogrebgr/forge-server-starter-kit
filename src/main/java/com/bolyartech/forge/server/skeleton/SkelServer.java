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

import static spark.Spark.awaitInitialization;
import static spark.Spark.stop;


public class SkelServer extends ForgeServerImpl {
    private static final String mPathPrefix = "/";

    public static final int DEFAULT_SESSION_TIMEOUT = 1440; // seconds

    private DbPool mDbPool;

    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private StringEndpointRegister mStringEndpointRegister;

    private VelocityTemplateEngine mTemplateEngine;


    @Override
    public void init() {
        super.init();

        try {
            mDbPool = createDbPool();
            mTemplateEngine = createVelocityTemplateEngine();

            RootRegister rootRegister = new RootRegisterImpl();
            mStringEndpointRegister = new StringEndpointRegisterImpl(rootRegister, DEFAULT_SESSION_TIMEOUT);

            initModules();
        } catch (Exception e) {
            mLogger.error("Error: {}", e);
            awaitInitialization();
            stop();
        }
    }


    private void initModules() {
        registerModule(new MainModule(mTemplateEngine, mPathPrefix, mStringEndpointRegister));
        registerModule(new UserModule(mDbPool, mPathPrefix, mStringEndpointRegister));
        registerModule(new AdminModule(mDbPool, mPathPrefix, mStringEndpointRegister));
    }


    public DbPool createDbPool() {
        DbConfiguration conf = ServerTools.loadDbConf("db.conf");
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
        serv.init();
    }
}
