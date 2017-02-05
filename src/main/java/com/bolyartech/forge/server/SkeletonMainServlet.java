package com.bolyartech.forge.server;

import com.bolyartech.forge.server.db.DbConfiguration;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.db.DbUtils;
import com.bolyartech.forge.server.module.HttpModule;
import com.bolyartech.forge.server.module.admin.AdminModule;
import com.bolyartech.forge.server.module.user.UserModule;
import com.bolyartech.forge.server.module.user_blowfish.BlowfishUserModule;
import com.bolyartech.forge.server.module.user_facebook.FacebookUserModule;
import com.bolyartech.forge.server.module.user_google.GoogleUserModule;
import com.bolyartech.forge.server.module.user_scram.UserScramModule;
import com.bolyartech.forge.server.modules.main.MainModule;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;


/**
 * Main servlet for the Forge Skeleton server app
 * <p>
 * If deployed inside a container (i.e. not using embeded jetty, tomcat, etc) you need to provide parameterless
 * constructor and load the configuration (staticFileDir, DbConfiguration)
 */
public class SkeletonMainServlet extends MainServlet {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass());

    private final String mStaticFileDir;
    private final DbConfiguration mDbConfiguration;

    private DbPool mDbPool;


    public SkeletonMainServlet(String staticFileDir, DbConfiguration dbConfiguration) {
        mStaticFileDir = staticFileDir;
        mDbConfiguration = dbConfiguration;
    }


    @Override
    public void init() throws ServletException {
        mDbPool = createDbPool();

        super.init();
    }


    @Override
    protected List<HttpModule> getModules() {
        List<HttpModule> ret = new ArrayList<>();

        ret.add(new MainModule(mStaticFileDir));
        ret.add(UserModule.createDefault(mDbPool));
        ret.add(AdminModule.createDefault(mDbPool));
        ret.add(UserScramModule.createDefault(mDbPool));
        ret.add(FacebookUserModule.createDefault(mDbPool));
        ret.add(GoogleUserModule.createDefault(mDbPool));
        ret.add(BlowfishUserModule.createDefault(mDbPool));

        return ret;
    }


    private DbPool createDbPool() {
        return DbUtils.createC3P0DbPool(mDbConfiguration);
    }
}
