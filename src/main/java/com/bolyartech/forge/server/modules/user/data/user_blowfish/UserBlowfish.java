package com.bolyartech.forge.server.modules.user.data.user_blowfish;

import com.bolyartech.forge.server.modules.user.data.blowfish.Blowfish;
import com.bolyartech.forge.server.modules.user.data.user.User;


public final class UserBlowfish {
    private final User mUser;
    private final Blowfish mBlowfish;


    public UserBlowfish(User user, Blowfish blowfish) {
        mUser = user;
        mBlowfish = blowfish;
    }


    public User getUser() {
        return mUser;
    }


    public Blowfish getBlowfish() {
        return mBlowfish;
    }
}
