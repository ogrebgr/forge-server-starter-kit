package com.bolyartech.forge.server.modules.admin.data;

import com.google.gson.annotations.SerializedName;


public class RokLoginAdmin {
    @SerializedName("session_ttl")
    public final int sessionTtl;

    @SerializedName("session_info")
    public final SessionInfoAdmin sessionInfo;

    public RokLoginAdmin(int sessionTtl, SessionInfoAdmin sessionInfo) {
        this.sessionTtl = sessionTtl;
        this.sessionInfo = sessionInfo;
    }
}
