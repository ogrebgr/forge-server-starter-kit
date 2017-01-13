package com.bolyartech.forge.server.modules.user.data.user_ext_id;

import java.sql.*;


public class UserExtIdDbhImpl implements UserExtIdDbh {
    @Override
    public UserExtId createNew(Connection dbc, long user, String extId, UserExtId.Type type) throws SQLException {

        String sql = "INSERT INTO user_ext_ids (user, ext_id, type) values (?, ?, ?)";
        try (PreparedStatement psInsert = dbc.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            psInsert.setLong(1, user);
            psInsert.setString(2, extId);
            psInsert.setInt(3, type.getCode());
            psInsert.executeUpdate();

            ResultSet rs = psInsert.getGeneratedKeys();
            rs.next();

            return new UserExtId(rs.getLong(1), user, extId, type);
        }
    }


    @Override
    public UserExtId loadByUser(Connection dbc, long user, UserExtId.Type type) throws SQLException {
        String sql = "SELECT id, ext_id FROM user_ext_ids WHERE user = ? and type = ?";
        try (PreparedStatement psLoad = dbc.prepareStatement(sql)) {
            psLoad.setLong(1, user);
            psLoad.setInt(2, type.getCode());

            try (ResultSet rs = psLoad.executeQuery()) {
                if (rs.next()) {
                    return new UserExtId(rs.getLong(1),
                            user,
                            rs.getString(2),
                            type);
                } else {
                    return null;
                }
            }
        }
    }


    @Override
    public UserExtId loadByExtId(Connection dbc, String extId, UserExtId.Type type) throws SQLException {
        String sql = "SELECT id, user FROM user_ext_ids WHERE ext = ? and type = ?";
        try (PreparedStatement psLoad = dbc.prepareStatement(sql)) {
            psLoad.setString(1, extId);
            psLoad.setInt(2, type.getCode());

            try (ResultSet rs = psLoad.executeQuery()) {
                if (rs.next()) {
                    return new UserExtId(rs.getLong(1),
                            rs.getLong(2),
                            extId,
                            type);
                } else {
                    return null;
                }
            }
        }
    }
}
