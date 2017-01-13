package com.bolyartech.forge.server.modules.user.data.user_ext_id;

import com.bolyartech.forge.server.modules.user.UserResponseCodes;

import java.util.HashMap;
import java.util.Map;


public final class UserExtId {
    public static final int TYPE_GOOGLE = 1;
    public static final int TYPE_FACEBOOK = 2;


    private final long mId;
    private final long mUser;
    private final String mExtId;
    private final Type mType;


    public UserExtId(long id, long user, String extId, Type type) {
        mId = id;
        mUser = user;
        mExtId = extId;
        mType = type;
    }


    public long getUser() {
        return mUser;
    }


    public Type getType() {
        return mType;
    }


    public long getId() {
        return mId;
    }


    public String getExtId() {
        return mExtId;
    }


    public enum Type {
        GOOGLE(1),
        FACEBOOK(2);

        private static final Map<Integer, UserResponseCodes.Errors> mTypesByValue = new HashMap<>();

        static {
            for (UserResponseCodes.Errors type : UserResponseCodes.Errors.values()) {
                mTypesByValue.put(type.getCode(), type);
            }
        }


        private final int mCode;


        Type(int code) {
            if (code < 0) {
                this.mCode = code;
            } else {
                throw new IllegalArgumentException("Code must be negative");
            }
        }


        public static UserResponseCodes.Errors fromInt(int code) {
            UserResponseCodes.Errors ret = mTypesByValue.get(code);
            if (ret != null) {
                return ret;
            } else {
                return null;
            }
        }


        public int getCode() {
            return mCode;
        }
    }
}
