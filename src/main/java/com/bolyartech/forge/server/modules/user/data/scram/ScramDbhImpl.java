package com.bolyartech.forge.server.modules.user.data.scram;

import com.google.common.base.Strings;

import java.sql.*;


public class ScramDbhImpl implements ScramDbh {

    @Override
    public Scram loadByUser(Connection dbc, long user) throws SQLException {
        if (user <= 0) {
            throw new IllegalStateException("user <= 0");
        }

        String sql = "SELECT username, salt, server_key, stored_key, iterations FROM user_scram WHERE user = ?";
        try (PreparedStatement psLoad = dbc.prepareStatement(sql)) {
            psLoad.setLong(1, user);

            try (ResultSet rs = psLoad.executeQuery()) {
                if (rs.next()) {
                    return new Scram(user,
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getInt(5)
                            );
                } else {
                    return null;
                }
            }

        }
    }


    @Override
    public Scram loadByUsername(Connection dbc, String username) throws SQLException {
        if (Strings.isNullOrEmpty(username)) {
            throw new IllegalStateException("username empty");
        }

        String sql = "SELECT user, salt, server_key, stored_key, iterations FROM user_scram WHERE username = ?";
        try (PreparedStatement psLoad = dbc.prepareStatement(sql)) {
            psLoad.setString(1, username);

            try (ResultSet rs = psLoad.executeQuery()) {
                if (rs.next()) {
                    return new Scram(rs.getLong(1),
                            username,
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getInt(5)
                    );
                } else {
                    return null;
                }
            }

        }
    }


    @Override
    public Scram createNew(Connection dbc, long user, String username, String salt, String serverKey, String storedKey,
                           int iterations) throws SQLException {

        Scram ret = new Scram(user, username, salt, serverKey, storedKey, iterations);

        String sql = "INSERT INTO user_scram (user, username, salt, server_key, stored_key, iterations) " +
                "VALUES (?,?,?,?,?,?)";

        try (PreparedStatement psInsert = dbc.prepareStatement(sql)) {
            psInsert.setLong(1, user);
            psInsert.setString(2, username);
            psInsert.setString(3, salt);
            psInsert.setString(4, serverKey);
            psInsert.setString(5, storedKey);
            psInsert.setInt(6, iterations);
            psInsert.executeUpdate();
        }

        return ret;
    }


    @Override
    public Scram change(Connection dbc, Scram old, String salt, String serverKey, String storedKey,
                        int iterations) throws SQLException {

        Scram ret = new Scram(old.getUser(), old.getUsername(), salt, serverKey,
                storedKey, iterations);

        String sql = "UPDATE user_scram SET " +
                "salt = ?," +
                "server_key = ?," +
                "stored_key = ?," +
                "iterations = ? " +
                "WHERE user = user";
        try (PreparedStatement psUpdate = dbc.prepareStatement(sql)) {
            psUpdate.setString(1, salt);
            psUpdate.setString(2, serverKey);
            psUpdate.setString(3, storedKey);
            psUpdate.setInt(4, iterations);
            psUpdate.executeUpdate();
        }

        return ret;
    }


    @Override
    public boolean delete(Connection dbc, Scram scram) throws SQLException {
        if (scram == null) {
            throw new NullPointerException("scram is null");
        }

        String sql = "DELETE FROM user_scram WHERE user = ?";
        try (PreparedStatement psUpdate = dbc.prepareStatement(sql)) {
            psUpdate.setLong(1, scram.getUser());
            int deleted = psUpdate.executeUpdate();
            return deleted > 0;
        }

    }
}
