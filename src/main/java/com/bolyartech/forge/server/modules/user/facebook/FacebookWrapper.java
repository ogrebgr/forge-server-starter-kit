package com.bolyartech.forge.server.modules.user.facebook;

public interface FacebookWrapper {
    SimpleFacebookUser checkToken(String token);
}
