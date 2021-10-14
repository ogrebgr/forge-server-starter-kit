package com.bolyartech.forge.server;

import com.bolyartech.forge.server.db.DbConfiguration;
import com.bolyartech.forge.server.db.DbPool;
import com.bolyartech.forge.server.db.DbUtils;
import com.bolyartech.forge.server.module.HttpModule;
import jakarta.servlet.ServletException;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Main servlet for the Forge Skeleton server app
 * <p>
 * If deployed inside a container (i.e. not using embeded jetty, tomcat, etc) you need to provide parameterless
 * constructor and load the configuration (staticFilesDir, DbConfiguration)
 */
public class StarterKitBaseServlet extends BaseServletDefaultImpl {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String staticFilesDir;
    private final DbConfiguration dbConfiguration;

    private DbPool mDbPool;


    public StarterKitBaseServlet(List<HttpModule> modules, String staticFilesDir, DbConfiguration dbConfiguration) {
        super(modules, true, 5);
        this.staticFilesDir = staticFilesDir;
        this.dbConfiguration = dbConfiguration;
    }


    @Override
    public void init() throws ServletException {
        mDbPool = createDbPool();

        super.init();
    }

//    @Override
//    protected List<HttpModule> getModules() {
//        List<HttpModule> ret = new ArrayList<>();
//
//        ret.add(new MainModule(staticFilesDir));
//        ret.add(UserModule.createDefault(mDbPool));
//        ret.add(AdminModule.createDefault(mDbPool));
//        ret.add(UserScramModule.createDefault(mDbPool));
//        ret.add(FacebookUserModule.createDefault(mDbPool));
//        ret.add(GoogleUserModule.createDefault(mDbPool));
//        ret.add(BlowfishUserModule.createDefault(mDbPool));
//
//        return ret;
//    }


    private DbPool createDbPool() {
        return DbUtils.createC3P0DbPool(dbConfiguration);
    }
}
