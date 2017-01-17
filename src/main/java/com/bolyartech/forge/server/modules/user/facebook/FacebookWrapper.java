package com.bolyartech.forge.server.modules.user.facebook;

public interface FacebookWrapper {
    ExternalUser checkToken(String token);
}
