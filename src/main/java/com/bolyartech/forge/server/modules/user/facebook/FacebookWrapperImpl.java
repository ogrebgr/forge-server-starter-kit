package com.bolyartech.forge.server.modules.user.facebook;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Version;


public class FacebookWrapperImpl implements FacebookWrapper {


    @Override
    public ExternalUser checkToken(String token) {
        FacebookClient fb = new DefaultFacebookClient(token, Version.VERSION_2_8);
        com.restfb.types.User fbUser = fb.fetchObject("me", com.restfb.types.User.class);

        if (fbUser != null) {
            return new ExternalUser(fbUser.getId(), fbUser.getEmail());
        } else {
            return null;
        }
    }
}
