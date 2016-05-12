package com.bolyartech.forge.server.skeleton.data;

import java.sql.*;


public class ScreenName {
    private static final String ANONYMOUS_USER_PREFIX = "user";

    private long mId;
    private long mUserId;
    private String mScreenName;
    private String mScreenNameLc;


    public ScreenName(long id, long userId, String screenName) {
        mId = id;
        mUserId = userId;
        mScreenName = screenName;
        mScreenNameLc = screenName.toLowerCase();
    }


    public static ScreenName loadById(Connection dbc, long id) throws SQLException {
        if (dbc.isClosed()) {
            throw new IllegalArgumentException("DB connection is closed.");
        }

        if (id <= 0) {
            throw new IllegalArgumentException("id invalid value: " + id);
        }

        String sql = "SELECT user, screen_name FROM user_screen_names WHERE id = ?";

        PreparedStatement psLoad = dbc.prepareStatement(sql);
        psLoad.setLong(1, id);
        ResultSet rs = psLoad.executeQuery();

        if (rs.next()) {
            return new ScreenName(id, rs.getLong(1), rs.getString(2));
        } else {
            return null;
        }
    }


    public static ScreenName loadByUser(Connection dbc, long userId) throws SQLException {
        if (dbc.isClosed()) {
            throw new IllegalArgumentException("DB connection is closed.");
        }

        if (userId <= 0) {
            throw new IllegalArgumentException("userId invalid value: " + userId);
        }

        String sql = "SELECT id, screen_name FROM user_screen_names WHERE id = ?";

        PreparedStatement psLoad = dbc.prepareStatement(sql);
        psLoad.setLong(1, userId);
        ResultSet rs = psLoad.executeQuery();

        if (rs.next()) {
            return new ScreenName(rs.getLong(1), userId, rs.getString(2));
        } else {
            return null;
        }
    }



    public static ScreenName createNew(Connection dbc, long userId, String screenName) throws SQLException {
        if (dbc.isClosed()) {
            throw new IllegalArgumentException("DB connection is closed.");
        }

        if (userId <= 0) {
            throw new IllegalArgumentException("userId invalid value: " + userId);
        }

        if (screenName == null) {
            throw new IllegalArgumentException("screenName is null");
        }

        PreparedStatement st = null;
        try {
            st = dbc.prepareStatement("INSERT INTO user_screen_names (user, screen_name, screen_name_lc) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            st.setLong(1, userId);
            st.setString(2, screenName);
            st.setString(3, screenName.toLowerCase());
            st.execute();

            ResultSet res = st.getGeneratedKeys();
            if (res.next()) {
                return new ScreenName(res.getLong(1), userId, screenName);
            } else {
                throw new IllegalStateException("No generated ID for user_screen_names");
            }
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }


    public static boolean exists(Connection dbc, String screenName) throws SQLException {
        if (dbc.isClosed()) {
            throw new IllegalArgumentException("DB connection is closed.");
        }

        if (screenName == null) {
            throw new IllegalArgumentException("screenName is null");
        }

        String sql = "SELECT id FROM user_screen_names WHERE screen_name_lc = ?";

        PreparedStatement psLoad = dbc.prepareStatement(sql);
        psLoad.setString(1, screenName.toLowerCase());
        ResultSet rs = psLoad.executeQuery();

        return rs.next();
    }


    public static boolean isValid(String screenName) {
        return screenName.matches("^[\\p{L}]{1}[\\p{L}\\p{N} ?]{1,33}[\\p{L}\\p{N}]{1}$");
    }


    public static String createDefault(long userId) {
        return ANONYMOUS_USER_PREFIX + userId;
    }


    public long getId() {
        return mId;
    }


    public long getUserId() {
        return mUserId;
    }


    public String getScreenName() {
        return mScreenName;
    }


    public String getScreenNameLc() {
        return mScreenNameLc;
    }
}
