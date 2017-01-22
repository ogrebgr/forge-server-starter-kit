package com.bolyartech.forge.server.modules.user.data;

public enum UserLoginType {
    UNKNOWN(-1),
    SCRAM(0),
    GOOGLE(1),
    FACEBOOK(2);

    private final long mCode;


    UserLoginType(long code) {
        mCode = code;
    }


    public static UserLoginType fromLong(long l) {
        if (l == 0) {
            return UserLoginType.SCRAM;
        }

        if (l == 1) {
            return UserLoginType.GOOGLE;
        }

        if (l == 2) {
            return UserLoginType.FACEBOOK;
        }

        return UserLoginType.UNKNOWN;
    }


    public long getCode() {
        return mCode;
    }
}
