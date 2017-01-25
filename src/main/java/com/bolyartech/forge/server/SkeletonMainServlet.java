package com.bolyartech.forge.server;

import com.bolyartech.forge.server.config.ForgeConfigurationException;
import com.bolyartech.forge.server.db.*;
import com.bolyartech.forge.server.module.HttpModule;
import com.bolyartech.forge.server.module.admin.AdminModule;
import com.bolyartech.forge.server.module.admin.data.*;
import com.bolyartech.forge.server.modules.main.MainModule;
import com.bolyartech.forge.server.module.user.BlowfishUserModule;
import com.bolyartech.forge.server.module.user.FacebookUserModule;
import com.bolyartech.forge.server.module.user.GoogleUserModule;
import com.bolyartech.forge.server.module.user.UserModule;
import com.bolyartech.forge.server.module.user.data.blowfish.BlowfishDbh;
import com.bolyartech.forge.server.module.user.data.blowfish.BlowfishDbhImpl;
import com.bolyartech.forge.server.module.user.data.scram.ScramDbh;
import com.bolyartech.forge.server.module.user.data.scram.ScramDbhImpl;
import com.bolyartech.forge.server.module.user.data.screen_name.ScreenNameDbh;
import com.bolyartech.forge.server.module.user.data.screen_name.ScreenNameDbhImpl;
import com.bolyartech.forge.server.module.user.data.user.UserDbh;
import com.bolyartech.forge.server.module.user.data.user.UserDbhImpl;
import com.bolyartech.forge.server.module.user.data.user_blowfish.UserBlowfishDbh;
import com.bolyartech.forge.server.module.user.data.user_blowfish.UserBlowfishDbhImpl;
import com.bolyartech.forge.server.module.user.data.user_ext_id.UserExtIdDbh;
import com.bolyartech.forge.server.module.user.data.user_ext_id.UserExtIdDbhImpl;
import com.bolyartech.forge.server.module.user.data.user_scram.UserScramDbh;
import com.bolyartech.forge.server.module.user.data.user_scram.UserScramDbhImpl;
import com.bolyartech.forge.server.module.user.facebook.FacebookWrapper;
import com.bolyartech.forge.server.module.user.facebook.FacebookWrapperImpl;
import com.bolyartech.forge.server.module.user.google.GoogleSignInWrapper;
import com.bolyartech.forge.server.module.user.google.GoogleSignInWrapperImpl;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.List;


public class SkeletonMainServlet extends MainServlet {
    private final org.slf4j.Logger mLogger = LoggerFactory.getLogger(this.getClass());

    private DbPool mDbPool;


    @Override
    public void init() throws ServletException {
        mDbPool = createDbPool();

        super.init();
    }


    @Override
    protected List<HttpModule> getModules() {
        List<HttpModule> ret = new ArrayList<>();

        UserScramDbh userScramDbh = new UserScramDbhImpl();
        UserDbh userDbh = new UserDbhImpl();
        ScramDbh scramDbh = new ScramDbhImpl();
        ScreenNameDbh screenNameDbh = new ScreenNameDbhImpl();
        UserExtIdDbh userExtIdDbh = new UserExtIdDbhImpl();
        ret.add(new MainModule());
        ret.add(new UserModule(mDbPool,
                userScramDbh,
                userDbh,
                scramDbh,
                screenNameDbh,
                userExtIdDbh,
                new GoogleSignInWrapperImpl()));
        ret.add(new AdminModule(mDbPool,
                new AdminUserDbhImpl(),
                scramDbh,
                new AdminScramDbhImpl(),
                userDbh,
                new AdminUserScramDbhImpl(),
                new UserExportedViewDbhImpl(),
                new AdminUserExportedViewDbhImpl()
        ));

        FacebookWrapper facebookWrapper = new FacebookWrapperImpl();
        ret.add(new FacebookUserModule(mDbPool, userDbh, scramDbh, screenNameDbh, userExtIdDbh, facebookWrapper));

        GoogleSignInWrapper googleSignInWrapper = new GoogleSignInWrapperImpl();
        ret.add(new GoogleUserModule(mDbPool, userDbh, scramDbh, screenNameDbh, userExtIdDbh, googleSignInWrapper));

        BlowfishDbh blowfishDbh = new BlowfishDbhImpl();
        UserBlowfishDbh userBlowfishDbh = new UserBlowfishDbhImpl();
        ret.add(new BlowfishUserModule(mDbPool, userBlowfishDbh, userDbh, blowfishDbh, screenNameDbh));

        return ret;
    }


    private DbPool createDbPool() {
        DbConfigurationLoader dbConfigurationLoader = new FileDbConfigurationLoader();
        try {
            DbConfiguration dbConfiguration = dbConfigurationLoader.load();
            return DbUtils.createC3P0DbPool(dbConfiguration);
        } catch (ForgeConfigurationException e) {
            mLogger.error("Cannot initialize SkeletonMainServlet", e);
            throw new RuntimeException(e);
        }
    }
}
