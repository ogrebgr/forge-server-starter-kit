package com.bolyartech.forge.server.skeleton.modules.api.login;

import com.bolyartech.forge.server.skeleton.json.SessionInfo;
import com.google.gson.annotations.SerializedName;

public class RokLogin {
    @SerializedName("session_ttl")
    public final int sessionTtl;
    @SerializedName("session_info")
    public final SessionInfo sessionInfo;


    public RokLogin(int sessionTtl, SessionInfo sessionInfo) {
        this.sessionTtl = sessionTtl;
        this.sessionInfo = sessionInfo;
    }
}
