package com.bolyartech.forge.server.modules.user.facebook;

import com.bolyartech.forge.server.modules.user.ExternalUser;


public interface FacebookWrapper {
    ExternalUser checkToken(String token);
}
