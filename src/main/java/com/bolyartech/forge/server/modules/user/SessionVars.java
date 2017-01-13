package com.bolyartech.forge.server.modules.user;

public final class SessionVars {
    public static final String VAR_USER = "mod_user__user";
    public static final String VAR_LOGIN_TYPE = "mod_user__login_type";
    public static final String VAR_SCRAM_FUNC = "mod_user__scram_func";
    public static final String VAR_SCRAM_DATA = "mod_user__scram_data";


    private SessionVars() {
        throw new AssertionError("Non-instantiable utility class");
    }
}
