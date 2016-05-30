package com.bolyartech.forge.server.modules.admin.data;
import com.google.gson.annotations.SerializedName;


public class RokLogin {
    @SerializedName("session_ttl")
    public final int sessionTtl;

    @SerializedName("superadmin")
    public final boolean superAdmin;


    public RokLogin(int sessionTtl, boolean superAdmin) {
        this.sessionTtl = sessionTtl;
        this.superAdmin = superAdmin;
    }
}
