package com.bolyartech.forge.server;

import com.bolyartech.forge.server.config.ForgeConfigurationException;
import com.bolyartech.forge.server.db.*;
import com.bolyartech.forge.server.module.HttpModule;
import com.bolyartech.forge.server.module.admin.AdminModule;
import com.bolyartech.forge.server.module.admin.data.*;
import com.bolyartech.forge.server.module.user_blowfish.BlowfishUserModule;
import com.bolyartech.forge.server.module.user_blowfish.data.BlowfishDbh;
import com.bolyartech.forge.server.module.user_blowfish.data.BlowfishDbhImpl;
import com.bolyartech.forge.server.module.user_blowfish.data.UserBlowfishDbh;
import com.bolyartech.forge.server.module.user_blowfish.data.UserBlowfishDbhImpl;
import com.bolyartech.forge.server.module.user_facebook.FacebookUserModule;
import com.bolyartech.forge.server.module.user_facebook.FacebookWrapper;
import com.bolyartech.forge.server.module.user_facebook.FacebookWrapperImpl;
import com.bolyartech.forge.server.module.user_google.GoogleSignInWrapper;
import com.bolyartech.forge.server.module.user_google.GoogleSignInWrapperImpl;
import com.bolyartech.forge.server.module.user_google.GoogleUserModule;
import com.bolyartech.forge.server.module.user_scram.data.scram.ScramDbh;
import com.bolyartech.forge.server.module.user_scram.data.scram.ScramDbhImpl;
import com.bolyartech.forge.server.module.user_scram.data.user_scram.UserScramDbh;
import com.bolyartech.forge.server.module.user_scram.data.user_scram.UserScramDbhImpl;
import com.bolyartech.forge.server.modules.main.MainModule;
import com.bolyartech.forge.server.module.user.UserModule;
import com.bolyartech.forge.server.module.user.data.screen_name.ScreenNameDbh;
import com.bolyartech.forge.server.module.user.data.screen_name.ScreenNameDbhImpl;
import com.bolyartech.forge.server.module.user.data.user.UserDbh;
import com.bolyartech.forge.server.module.user.data.user.UserDbhImpl;
import com.bolyartech.forge.server.module.user.data.user_ext_id.UserExtIdDbh;
import com.bolyartech.forge.server.module.user.data.user_ext_id.UserExtIdDbhImpl;
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
        ret.add(new UserModule(mDbPool, screenNameDbh));
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
        ret.add(new FacebookUserModule(mDbPool, userDbh, screenNameDbh, userExtIdDbh, facebookWrapper));

        GoogleSignInWrapper googleSignInWrapper = new GoogleSignInWrapperImpl();
        ret.add(new GoogleUserModule(mDbPool, userDbh, screenNameDbh, userExtIdDbh, googleSignInWrapper));

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
