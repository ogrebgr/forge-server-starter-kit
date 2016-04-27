package com.bolyartech.forge.server.skeleton.data;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.UUID;

public class User {
    private long mId;
    private String mUsername;
    private String mEncryptedPassword;
    private boolean mIsDisabled;


    public User(long id, String username, String encryptedPassword, boolean isDisabled) {
        mId = id;
        mUsername = username;
        mEncryptedPassword = encryptedPassword;
        mIsDisabled = isDisabled;
    }


    public static User loadById(Connection dbc, long id) throws SQLException {
        String sql = "SELECT username, password, is_disabled FROM users WHERE id = ?";

        PreparedStatement psLoad = dbc.prepareStatement(sql);
        psLoad.setLong(1, id);
        ResultSet rs = psLoad.executeQuery();

        if (rs.next()) {
            return new User(id, rs.getString(1), rs.getString(2), rs.getInt(3) == 1);
        } else {
            return null;
        }
    }


    public static User generateAnonymousUser(Connection dbc) throws SQLException {
        Statement lockSt = dbc.createStatement();
        lockSt.execute("LOCK TABLES users WRITE");

        String username;
        do {
            username = UUID.randomUUID().toString();
        } while (usernameExists(dbc, username));

        User user = createNewUser(dbc, username, UUID.randomUUID().toString(), false);

        Statement unlockSt = dbc.createStatement();
        unlockSt.execute("UNLOCK TABLES");

        return user;
    }


    private static boolean usernameExists(Connection dbc, String username) throws SQLException {
        PreparedStatement st = null;
        boolean exist = false;
        try {
            st = dbc.prepareStatement("SELECT id FROM users WHERE username = ?");
            st.setString(1, username);

            ResultSet res = st.executeQuery();
            exist = res.next();
        } finally {
            if (st != null) {
                st.close();
            }
        }

        return exist;
    }


    private static User createNewUser(Connection dbc, String username, String password, boolean isDisabled) throws SQLException {
        PreparedStatement st = null;
        try {
            st = dbc.prepareStatement("INSERT INTO users (username, `password`, is_disabled) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, username);
            String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
            st.setString(2, encryptedPassword);
            st.setInt(3, isDisabled ? 1 : 0);
            st.execute();

            ResultSet res = st.getGeneratedKeys();
            if (res.next()) {
                return new User(res.getLong(1), username, encryptedPassword, isDisabled);
            } else {
                throw new IllegalStateException("No generated ID for user");
            }
        } finally {
            if (st != null) {
                st.close();
            }
        }
    }


    public long getId() {
        return mId;
    }


    public String getUsername() {
        return mUsername;
    }


    public String getEncryptedPassword() {
        return mEncryptedPassword;
    }


    public boolean isDisabled() {
        return mIsDisabled;
    }
}
