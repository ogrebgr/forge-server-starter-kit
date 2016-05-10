package com.bolyartech.forge.server.skeleton;

import com.bolyartech.forge.server.ForgeServerImpl;
import com.bolyartech.forge.server.config.DbConfiguration;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.misc.ServerTools;
import com.bolyartech.forge.server.register.MavEndpointRegister;
import com.bolyartech.forge.server.register.MavEndpointRegisterImpl;
import com.bolyartech.forge.server.skeleton.modules.api.ApiModule;
import com.bolyartech.forge.server.skeleton.modules.main.MainModule;
import org.slf4j.LoggerFactory;
import spark.TemplateEngine;
import spark.template.velocity.VelocityTemplateEngine;

import static spark.Spark.awaitInitialization;
import static spark.Spark.get;
import static spark.Spark.stop;


public class SkelServer extends ForgeServerImpl {
    public static final int DEFAULT_SESSION_TIMEOUT = 1440; // seconds

    private DbPool mDbPool;

    private TemplateEngine mTemplateEngine;

    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private MavEndpointRegister mMavEndpointRegister;


    @Override
    public void init() {
        super.init();

        try {
            mDbPool = createDbPool();
            mTemplateEngine = createTemplateEngine();
            mMavEndpointRegister = new MavEndpointRegisterImpl(getServerConfiguration().getSessionTimeout(), mTemplateEngine);

            initModules();
        } catch (Exception e) {
            awaitInitialization();
            stop();
        }
    }


    private void initModules() {
        MainModule tm = new MainModule(mDbPool);
        tm.registerEndpoints(mMavEndpointRegister);


        ApiModule apiMod = new ApiModule(mDbPool);
        apiMod.registerEndpoints(mMavEndpointRegister);
    }


    public DbPool createDbPool() {
        DbConfiguration conf = ServerTools.loadDbConf("db.conf");
        if (conf != null) {
            return ServerTools.createComboPooledDataSource(conf);
        } else {
            return null;
        }
    }


    private TemplateEngine createTemplateEngine() {
        return new VelocityTemplateEngine();
    }


    public static void main(String[] args) {
        SkelServer serv = new SkelServer();
        serv.init();
    }
}
