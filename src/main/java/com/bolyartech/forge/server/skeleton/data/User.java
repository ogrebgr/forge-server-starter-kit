package com.bolyartech.forge.server.skeleton.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private long mId;
    private String mUsername;
    private boolean mIsDisabled;


    public User(long id, String username, boolean isDisabled) {
        mId = id;
        mUsername = username;
        mIsDisabled = isDisabled;
    }


    public static User loadById(Connection dbc, long id) throws SQLException {
        String sql = "SELECT username, is_disabled FROM users WHERE id = ?";

        PreparedStatement psLoad = dbc.prepareStatement(sql);
        psLoad.setLong(1, id);
        ResultSet rs = psLoad.executeQuery();

        if (rs.next()) {
            return new User(id, rs.getString(1), rs.getInt(2) == 1);
        } else {
            return null;
        }
    }


    public long getId() {
        return mId;
    }


    public String getUsername() {
        return mUsername;
    }


    public boolean isDisabled() {
        return mIsDisabled;
    }
}
