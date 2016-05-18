package com.bolyartech.forge.server.modules.admin.data;

import com.google.gson.annotations.SerializedName;

public class AdminUserJson {
    @SerializedName("id")
    public final long mId;
    @SerializedName("username")
    public final String mUsername;
    @SerializedName("is_disabled")
    public final boolean mIsDisabled;
    @SerializedName("is_super_admin")
    public final boolean mIsSuperAdmin;
    @SerializedName("name")
    public final String mName;


    public AdminUserJson(AdminUser user) {
        mId = user.getId();
        mUsername = user.getUsername();
        mIsDisabled = user.isDisabled();
        mIsSuperAdmin = user.isSuperAdmin();
        mName = user.getName();
    }
}
