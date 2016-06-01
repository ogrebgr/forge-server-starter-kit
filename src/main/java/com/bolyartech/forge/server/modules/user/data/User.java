package com.bolyartech.forge.server.modules.user.data;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private static final org.slf4j.Logger sLogger = LoggerFactory.getLogger("User");

    private final long mId;
    private final String mUsername;
    private final String mEncryptedPassword;
    private final boolean mIsDisabled;


    public User(long id, String username, String encryptedPassword, boolean isDisabled) {
        mId = id;
        mUsername = username;
        mEncryptedPassword = encryptedPassword;
        mIsDisabled = isDisabled;
    }


    public static User loadById(Connection dbc, long id) throws SQLException {
        if (dbc.isClosed()) {
            throw new IllegalArgumentException("DB connection is closed.");
        }


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


    public static User loadByUsername(Connection dbc, String username) throws SQLException {
        if (dbc.isClosed()) {
            throw new IllegalArgumentException("DB connection is closed.");
        }

        String sql = "SELECT id, password, is_disabled FROM users WHERE username = ?";

        PreparedStatement psLoad = dbc.prepareStatement(sql);
        psLoad.setString(1, username);
        ResultSet rs = psLoad.executeQuery();

        if (rs.next()) {
            return new User(rs.getLong(1), username, rs.getString(2), rs.getInt(3) == 1);
        } else {
            return null;
        }
    }


    public static AnonymousUserHelper generateAnonymousUser(Connection dbc) throws SQLException {
        if (dbc.isClosed()) {
            throw new IllegalArgumentException("DB connection is closed.");
        }


        Statement lockSt = dbc.createStatement();
        lockSt.execute("LOCK TABLES users WRITE");

        String username;
        do {
            username = UUID.randomUUID().toString();
        } while (usernameExists(dbc, username));

        String password = UUID.randomUUID().toString();
        User user = createNew(dbc, username, password, false);

        Statement unlockSt = dbc.createStatement();
        unlockSt.execute("UNLOCK TABLES");

        return new AnonymousUserHelper(user, password);
    }


    public void auto2registered(Connection dbc, String username, String password, String screenName) throws SQLException {
        String encryptedPassword = encryptPassword(password);

        PreparedStatement st = null;
        try {
            dbc.setAutoCommit(false);
            st = dbc.prepareStatement("UPDATE users SET username = ?, password = ? WHERE id = ?");
            st.setString(1, username);
            st.setString(2, encryptedPassword);
            st.setLong(3, getId());
            st.execute();

            ScreenName.createNew(dbc, getId(), screenName);
        } catch (Exception e) {
            sLogger.error("DB error {}", e);
            dbc.rollback();
            throw e;
        } finally {
            if (st != null) {
                st.close();
            }

            dbc.setAutoCommit(true);
        }
    }


    public static boolean usernameExists(Connection dbc, String username) throws SQLException {
        if (dbc.isClosed()) {
            throw new IllegalArgumentException("DB connection is closed.");
        }


        boolean exist = false;
        try (PreparedStatement st = dbc.prepareStatement("SELECT id FROM users WHERE username = ?")) {
            st.setString(1, username);

            ResultSet res = st.executeQuery();
            exist = res.next();
        } catch (Exception e) {
            int i = 1;
            i++;
        }

        return exist;
    }


    private static User createNew(Connection dbc, String username, String password, boolean isDisabled) throws SQLException {
        PreparedStatement st = null;
        try {
            st = dbc.prepareStatement("INSERT INTO users (username, `password`, is_disabled) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            st.setString(1, username);
            String encryptedPassword = encryptPassword(password);
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


    public static User createNew(Connection dbc, String username, String password, boolean isDisabled, String screenName) throws SQLException {
        try {
            dbc.setAutoCommit(false);
            User user = createNew(dbc, username, password, isDisabled);
            ScreenName.createNew(dbc, user.getId(), screenName);
            return user;
        } catch (Exception e) {
            sLogger.error("DB error {}", e);
            dbc.rollback();
            throw e;
        } finally {
            dbc.setAutoCommit(true);
        }
    }

    public static User checkLogin(Connection dbc, String username, String password) throws SQLException {
        if (dbc.isClosed()) {
            throw new IllegalArgumentException("DB connection is closed.");
        }

        User user = User.loadByUsername(dbc, username);
        if (user != null) {
            if (BCrypt.checkpw(password, user.getEncryptedPassword())) {
                return user;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    private static String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
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


    public static boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z]{1}[a-zA-Z0-9 _.?]{1,48}[a-zA-Z0-9]{1}$");
    }


    public static class AnonymousUserHelper {
        public final User mUser;
        public final String mClearPassword;


        public AnonymousUserHelper(User user, String clearPassword) {
            mUser = user;
            mClearPassword = clearPassword;
        }
    }
}
