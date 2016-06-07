package com.bolyartech.forge.server.modules.swoon.data;

import org.slf4j.LoggerFactory;

import java.sql.*;


public class InteractionDb implements Interaction {
    private static final org.slf4j.Logger sLogger = LoggerFactory.getLogger("User");

    private Interaction mInteraction;


    public InteractionDb(Interaction interaction) {
        mInteraction = interaction;
    }


    public static Interaction loadById(Connection dbc, long id) throws SQLException {
        if (dbc.isClosed()) {
            throw new IllegalArgumentException("DB connection is closed.");
        }

        String sql = "SELECT type, is_dismissable, is_postponable, is_modal FROM interactions WHERE id = ?";

        PreparedStatement psLoad = dbc.prepareStatement(sql);
        psLoad.setLong(1, id);
        ResultSet rs = psLoad.executeQuery();

        if (rs.next()) {
            return new InteractionImpl(id, rs.getLong(1), rs.getInt(2) == 1, rs.getInt(3) == 1, rs.getInt(4) == 1);
        } else {
            return null;
        }
    }


    public long save(Connection dbc) throws SQLException {
        if (mInteraction.getId() == 0) {
            return insert(dbc);
        } else {
            int count = update(dbc);
            if (count != 1) {
                throw new IllegalStateException("Unexpected count of updated rows");
            }
            return mInteraction.getId();
        }
    }


    private int update(Connection dbc) throws SQLException {
        String sql = "UPDATE interactions SET type = ?, is_dismissable = ?, is_postponable = ?, is_modal = ?";

        try (PreparedStatement st = dbc.prepareStatement(sql)) {
            st.setLong(1, mInteraction.getType());
            st.setInt(2, mInteraction.isDismissable() ? 1 : 0);
            st.setInt(3, mInteraction.isPostponable() ? 1 : 0);
            st.setInt(4, mInteraction.isModal() ? 1 : 0);

            return st.executeUpdate();
        }
    }


    private long insert(Connection dbc) throws SQLException {
        String sql = "INSERT INTO interactions (type, is_dismissable, is_postponable, is_modal) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement st = dbc.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setLong(1, mInteraction.getType());
            st.setInt(2, mInteraction.isDismissable() ? 1 : 0);
            st.setInt(3, mInteraction.isPostponable() ? 1 : 0);
            st.setInt(4, mInteraction.isModal() ? 1 : 0);


            ResultSet res = st.getGeneratedKeys();
            if (res.next()) {
                mInteraction = new InteractionImpl(res.getLong(1),
                        mInteraction.getType(),
                        mInteraction.isDismissable(),
                        mInteraction.isPostponable(),
                        mInteraction.isModal());

                return mInteraction.getId();
            } else {
                throw new IllegalStateException("No generated ID");
            }
        }
    }


    @Override
    public long getId() {
        return mInteraction.getId();
    }


    @Override
    public long getType() {
        return mInteraction.getType();
    }


    @Override
    public boolean isDismissable() {
        return mInteraction.isDismissable();
    }


    @Override
    public boolean isPostponable() {
        return mInteraction.isPostponable();
    }


    @Override
    public boolean isModal() {
        return mInteraction.isModal();
    }
}
