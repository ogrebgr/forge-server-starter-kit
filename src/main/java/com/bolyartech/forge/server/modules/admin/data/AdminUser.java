package com.bolyartech.forge.server.modules.admin.data;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AdminUser {
    private static final int MIN_PASSWORD_LENGTH = 7;
    private static final org.slf4j.Logger sLogger = LoggerFactory.getLogger("User");


    private final long mId;
    private final String mUsername;
    private final String mEncryptedPassword;
    private final boolean mIsDisabled;
    private final boolean mSuperAdmin;
    private final String mName;


    public AdminUser(long id, String username, String encryptedPassword, boolean isDisabled, boolean superAdmin, String name) {
        mId = id;
        mUsername = username;
        mEncryptedPassword = encryptedPassword;
        mIsDisabled = isDisabled;
        mSuperAdmin = superAdmin;
        mName = name;
    }


    public static AdminUser loadById(Connection dbc, long id) throws SQLException {
        if (dbc.isClosed()) {
            throw new IllegalArgumentException("DB connection is closed.");
        }


        String sql = "SELECT username, password, is_disabled, is_super_admin, name FROM admin_users WHERE id = ?";

        try (PreparedStatement psLoad = dbc.prepareStatement(sql)) {
            psLoad.setLong(1, id);
            try (ResultSet rs = psLoad.executeQuery()) {
                if (rs.next()) {
                    return new AdminUser(id, rs.getString(1), rs.getString(2), rs.getInt(3) == 1, rs.getInt(4) == 1, rs.getString(5));
                } else {
                    return null;
                }
            }
        }
    }


    public static AdminUser loadByUsername(Connection dbc, String username) throws SQLException {
        if (dbc.isClosed()) {
            throw new IllegalArgumentException("DB connection is closed.");
        }

        String sql = "SELECT id, password, is_disabled, is_super_admin, name FROM admin_users WHERE username = ?";

        PreparedStatement psLoad = dbc.prepareStatement(sql);
        psLoad.setString(1, username);
        ResultSet rs = psLoad.executeQuery();

        if (rs.next()) {
            return new AdminUser(rs.getLong(1), username, rs.getString(2), rs.getInt(3) == 1, rs.getInt(4) == 1, rs.getString(5));
        } else {
            return null;
        }
    }


    public static boolean usernameExists(Connection dbc, String username) throws SQLException {
        if (dbc.isClosed()) {
            throw new IllegalArgumentException("DB connection is closed.");
        }


        boolean exist;
        try (PreparedStatement st = dbc.prepareStatement("SELECT id FROM admin_users WHERE username = ?")) {
            st.setString(1, username);

            ResultSet res = st.executeQuery();
            exist = res.next();
        } catch (SQLException e) {
            sLogger.error("SQL error: ", e);
            throw e;
        }

        return exist;
    }


    @SuppressWarnings("UnusedReturnValue")
    public static AdminUser createNew(Connection dbc, String username, String password, boolean isDisabled, boolean isSuperAdmin, String name) throws SQLException {
        String sql = "INSERT INTO admin_users (username, `password`, is_disabled, is_super_admin, name) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement st = dbc.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, username);
            String encryptedPassword = encryptPassword(password);
            st.setString(2, encryptedPassword);
            st.setInt(3, isDisabled ? 1 : 0);
            st.setInt(4, isSuperAdmin ? 1 : 0);
            st.setString(5, name);
            st.execute();

            ResultSet res = st.getGeneratedKeys();
            if (res.next()) {
                return new AdminUser(res.getLong(1), username, encryptedPassword, isDisabled, isSuperAdmin, name);
            } else {
                throw new IllegalStateException("No generated ID for user");
            }
        } catch (SQLException e) {
            sLogger.error("SQL error: ", e);
            throw e;
        }
    }


    public static AdminUser checkLogin(Connection dbc, String username, String password) throws SQLException {
        if (dbc.isClosed()) {
            throw new IllegalArgumentException("DB connection is closed.");
        }

        AdminUser user = AdminUser.loadByUsername(dbc, username);
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


    public static List<AdminUser> list(Connection dbc) throws SQLException {
        String sql = "SELECT id FROM admin_users ORDER BY id ASC";

        List<AdminUser> ret = new ArrayList<>();
        try (PreparedStatement st = dbc.prepareStatement(sql)) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                AdminUser tmp = AdminUser.loadById(dbc, rs.getLong(1));
                if (tmp != null) {
                    ret.add(tmp);
                }
            }
        }

        return ret;
    }


    public static boolean disable(Connection dbc, long userId, boolean disable) throws SQLException {
        if (userId <= 0) {
            throw new IllegalStateException("Invalid userId: " + userId);
        }

        String sql = "UPDATE admin_users SET is_disabled = ? WHERE id = ?";
        try (PreparedStatement st = dbc.prepareStatement(sql)) {
            st.setInt(1, disable ? 1 : 0);
            st.setLong(2, userId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            sLogger.error("SQL error: ", e);
            throw e;
        }
    }


    @SuppressWarnings("UnusedReturnValue")
    public static boolean changePassword(Connection dbc, long userId, String newPassword) throws SQLException {
        if (newPassword == null) {
            throw new IllegalArgumentException("password is null");
        }

        if (!isValidPasswordLength(newPassword)) {
            throw new IllegalArgumentException("Password is too short");
        }

        String encryptedPassword = encryptPassword(newPassword);

        String sql = "UPDATE admin_users SET password = ? WHERE id = ?";
        try (PreparedStatement st = dbc.prepareStatement(sql)) {
            st.setString(1, encryptedPassword);
            st.setLong(2, userId);
            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            sLogger.error("SQL error: ", e);
            throw e;
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


    public boolean isSuperAdmin() {
        return mSuperAdmin;
    }


    private static String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }


    public String getName() {
        return mName;
    }


    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidPasswordLength(String password) {
        if (password == null) {
            throw new IllegalArgumentException("password is null");
        }

        return password.length() >= MIN_PASSWORD_LENGTH;
    }


    public static boolean isValidName(String name) {
        //noinspection SimplifiableIfStatement
        if (name == null) {
            return false;
        }

        return name.matches("^[a-zA-Z][a-zA-Z0-9 _.?]{1,49}[a-zA-Z0-9]$");
    }
}
