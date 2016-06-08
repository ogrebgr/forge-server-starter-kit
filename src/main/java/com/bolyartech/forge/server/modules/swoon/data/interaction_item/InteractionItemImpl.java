package com.bolyartech.forge.server.modules.swoon.data.interaction_item;

import com.bolyartech.forge.server.db.DbObject;

import java.sql.*;

public class InteractionItemImpl implements InteractionItem, DbObject<InteractionItem> {
    private final long mId;
    private final long mInteraction;
    private final long mOrdering;
    private final long mType;



    public InteractionItemImpl(long id, long interaction, long ordering, long type) {
        mId = id;
        mInteraction = interaction;
        mOrdering = ordering;
        mType = type;
    }


    @Override
    public long getId() {
        return mId;
    }


    @Override
    public long getInteraction() {
        return mInteraction;
    }


    @Override
    public long getOrdering() {
        return mOrdering;
    }


    @Override
    public long getType() {
        return mType;
    }


    public InteractionItem save(Connection dbc) throws SQLException {
        if (mId == 0) {
            return insert(dbc);
        } else {
            return update(dbc);
        }
    }


    private InteractionItem update(Connection dbc) throws SQLException {
        String sql = "UPDATE interaction_items SET interaction = ?, ordering = ?, type = ? WHERE id = ?";

        try (PreparedStatement st = dbc.prepareStatement(sql)) {
            st.setLong(1, getInteraction());
            st.setLong(2, getOrdering());
            st.setLong(3, getType());
            st.setLong(4, getId());

            int count = st.executeUpdate();
            if (count != 1) {
                throw new IllegalStateException("Unexpected count of updated rows");
            }

            return new InteractionItemImpl(getId(),
                    getInteraction(),
                    getOrdering(),
                    getType()
            );
        }
    }


    private InteractionItem insert(Connection dbc) throws SQLException {
        String sql = "INSERT INTO interaction_items (interaction, ordering, type) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement st = dbc.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setLong(1, getInteraction());
            st.setLong(2, getOrdering());
            st.setLong(3, getType());


            ResultSet res = st.getGeneratedKeys();
            if (res.next()) {
                return new InteractionItemImpl(res.getLong(1),
                        getInteraction(),
                        getOrdering(),
                        getType()
                        );
            } else {
                throw new IllegalStateException("No generated ID");
            }
        }
    }


    public static InteractionItem loadById(Connection dbc, long id) throws SQLException {
        String sql = "SELECT interaction, ordering, type FROM interaction_items WHERE id = ?";

        try (PreparedStatement st = dbc.prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new InteractionItemImpl(id, rs.getLong(1), rs.getLong(2), rs.getLong(3));
                } else {
                    return null;
                }
            }
        }
    }
}
