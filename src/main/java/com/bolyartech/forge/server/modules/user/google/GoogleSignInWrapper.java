package com.bolyartech.forge.server.modules.user.google;

import com.bolyartech.forge.server.modules.user.facebook.ExternalUser;


public interface GoogleSignInWrapper {
    ExternalUser checkToken(String token);
}
