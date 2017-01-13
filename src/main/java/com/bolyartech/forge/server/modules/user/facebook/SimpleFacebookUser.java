package com.bolyartech.forge.server.modules.user.facebook;

public class SimpleFacebookUser {
    private final String mFacebookId;
    private final String mEmail;


    public SimpleFacebookUser(String facebookId, String email) {
        mFacebookId = facebookId;
        mEmail = email;
    }


    public String getFacebookId() {
        return mFacebookId;
    }


    public String getEmail() {
        return mEmail;
    }
}
