package com.bolyartech.forge.server.modules.user.data;

import com.google.gson.annotations.SerializedName;

public class SessionInfo {
    @SerializedName("user_id")
    public final long user_id;
    @SerializedName("screen_name_chosen")
    public final String screenNameChosen;
    @SerializedName("screen_name_default")
    public final String screenNameDefault;

    public SessionInfo(long userId, String screenNameChosen, String screenNameDefault) {
        this.user_id = userId;
        this.screenNameChosen = screenNameChosen;
        this.screenNameDefault = screenNameDefault;
    }
}
