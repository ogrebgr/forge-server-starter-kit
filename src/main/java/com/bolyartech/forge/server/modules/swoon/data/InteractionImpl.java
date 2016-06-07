package com.bolyartech.forge.server.modules.swoon.data;

import java.sql.*;

public class InteractionImpl implements Interaction {
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


    public static InteractionImpl save(Connection dbc, InteractionImpl interaction) throws SQLException {
        if (interaction.getId() == 0) {
            return insert(dbc, interaction);
        } else {
            return update(dbc, interaction);
        }
    }


    private static InteractionImpl update(Connection dbc, InteractionImpl interaction) throws SQLException {
        String sql = "UPDATE interactions SET type = ?, is_dismissable = ?, is_postponable = ?, is_modal = ?";

        try (PreparedStatement st = dbc.prepareStatement(sql)) {
            st.setLong(1, interaction.getType());
            st.setInt(2, interaction.isDismissable() ? 1 : 0);
            st.setInt(3, interaction.isPostponable() ? 1 : 0);
            st.setInt(4, interaction.isModal() ? 1 : 0);

            int count = st.executeUpdate();
            if (count != 1) {
                throw new IllegalStateException("Unexpected count of updated rows");
            }

            return new InteractionImpl(interaction.getId(),
                    interaction.getType(),
                    interaction.isDismissable(),
                    interaction.isPostponable(),
                    interaction.isModal());
        }
    }


    private static InteractionImpl insert(Connection dbc, InteractionImpl interaction) throws SQLException {
        String sql = "INSERT INTO interactions (type, is_dismissable, is_postponable, is_modal) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement st = dbc.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setLong(1, interaction.getType());
            st.setInt(2, interaction.isDismissable() ? 1 : 0);
            st.setInt(3, interaction.isPostponable() ? 1 : 0);
            st.setInt(4, interaction.isModal() ? 1 : 0);


            ResultSet res = st.getGeneratedKeys();
            if (res.next()) {
                return new InteractionImpl(res.getLong(1),
                        interaction.getType(),
                        interaction.isDismissable(),
                        interaction.isPostponable(),
                        interaction.isModal());
            } else {
                throw new IllegalStateException("No generated ID");
            }
        }
    }
}
