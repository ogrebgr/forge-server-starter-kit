package com.bolyartech.forge.server.modules.user.google;

import com.bolyartech.forge.server.modules.user.ExternalUser;


public interface GoogleSignInWrapper {
    ExternalUser checkToken(String token);
}
