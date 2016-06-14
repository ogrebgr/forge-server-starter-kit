package com.bolyartech.forge.server.modules.swoon.data.interaction;

import com.bolyartech.forge.server.db.DbObject;

import java.sql.*;

public class InteractionImpl implements Interaction, DbObject<Interaction> {
    private final long mId;
    private final long mType;
    private final boolean mIsDismissable;
    private final boolean mIsPostponable;
    private final boolean mIsModal;


    public InteractionImpl(long id, long type, boolean isDismissable, boolean isPostponable, boolean isModal) {
        mId = id;
        mType = type;
        mIsDismissable = isDismissable;
        mIsPostponable = isPostponable;
        mIsModal = isModal;
    }


    @Override
    public long getId() {
        return mId;
    }


    @Override
    public long getType() {
        return mType;
    }


    @Override
    public boolean isDismissable() {
        return mIsDismissable;
    }


    @Override
    public boolean isPostponable() {
        return mIsPostponable;
    }


    @Override
    public boolean isModal() {
        return mIsModal;
    }


    @Override
    public InteractionImpl save(Connection dbc) throws SQLException {
        if (mId == 0) {
            return insert(dbc);
        } else {
            return update(dbc);
        }
    }


    public static Interaction loadById(Connection dbc, long id) throws SQLException {
        String sql = "SELECT type, is_dismissable, is_postponable, is_modal FROM interaction_items WHERE id = ?";

        try (PreparedStatement st = dbc.prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new InteractionImpl(id,
                            rs.getLong(1),
                            rs.getInt(2) == 1,
                            rs.getInt(3) == 1,
                            rs.getInt(4) == 1);
                } else {
                    return null;
                }
            }
        }
    }


    private InteractionImpl update(Connection dbc) throws SQLException {
        String sql = "UPDATE interactions SET type = ?, is_dismissable = ?, is_postponable = ?, is_modal = ? WHERE id = ?";

        try (PreparedStatement st = dbc.prepareStatement(sql)) {
            st.setLong(1, getType());
            st.setInt(2, isDismissable() ? 1 : 0);
            st.setInt(3, isPostponable() ? 1 : 0);
            st.setInt(4, isModal() ? 1 : 0);
            st.setLong(5, getId());

            int count = st.executeUpdate();
            if (count != 1) {
                throw new IllegalStateException("Unexpected count of updated rows");
            }

            return new InteractionImpl(getId(),
                    getType(),
                    isDismissable(),
                    isPostponable(),
                    isModal());
        }
    }


    private InteractionImpl insert(Connection dbc) throws SQLException {
        String sql = "INSERT INTO interactions (type, is_dismissable, is_postponable, is_modal) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement st = dbc.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setLong(1, getType());
            st.setInt(2, isDismissable() ? 1 : 0);
            st.setInt(3, isPostponable() ? 1 : 0);
            st.setInt(4, isModal() ? 1 : 0);


            ResultSet res = st.getGeneratedKeys();
            if (res.next()) {
                return new InteractionImpl(res.getLong(1),
                        getType(),
                        isDismissable(),
                        isPostponable(),
                        isModal());
            } else {
                throw new IllegalStateException("No generated ID");
            }
        }
    }
}
