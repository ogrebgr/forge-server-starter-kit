package com.bolyartech.forge.server.modules.user;

public class ExternalUser {
    private final String mExternalId;
    private final String mEmail;


    public ExternalUser(String externalId, String email) {
        mExternalId = externalId;
        mEmail = email;
    }


    public String getExternalId() {
        return mExternalId;
    }


    public String getEmail() {
        return mEmail;
    }
}
