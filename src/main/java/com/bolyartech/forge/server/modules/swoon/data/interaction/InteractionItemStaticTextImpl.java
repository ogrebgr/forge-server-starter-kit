package com.bolyartech.forge.server.modules.swoon.data.interaction;

import com.bolyartech.forge.server.db.DbObject;

import java.sql.*;


public class InteractionItemStaticTextImpl implements InteractionItemStaticText, DbObject<InteractionItemStaticText> {
    private final long mId;
    private final long mItem;
    private final String mText;


    public InteractionItemStaticTextImpl(long id, long item, String text) {
        mId = id;
        mItem = item;
        mText = text;
    }


    @Override
    public long getId() {
        return mId;
    }


    @Override
    public long getItem() {
        return mItem;
    }


    @Override
    public String getText() {
        return mText;
    }


    @Override
    public InteractionItemStaticText save(Connection dbc) throws SQLException {
        if (mId == 0) {
            return insert(dbc);
        } else {
            return update(dbc);
        }
    }


    private InteractionItemStaticText update(Connection dbc) throws SQLException {
        String sql = "UPDATE interaction_static_items_text SET item == ?, text_content == ? WHERE id = ?";

        try (PreparedStatement st = dbc.prepareStatement(sql)) {
            st.setLong(1, getItem());
            st.setString(2, getText());

            int count = st.executeUpdate();
            if (count != 1) {
                throw new IllegalStateException("Unexpected count of updated rows");
            }

            return new InteractionItemStaticTextImpl(getId(),
                    getItem(),
                    getText()
            );
        }
    }


    private InteractionItemStaticText insert(Connection dbc) throws SQLException {
        String sql = "INSERT INTO interaction_static_items_text (item, text_content) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement st = dbc.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setLong(1, getItem());
            st.setString(2, getText());


            ResultSet res = st.getGeneratedKeys();
            if (res.next()) {
                return new InteractionItemStaticTextImpl(res.getLong(1),
                        getItem(),
                        getText()
                );
            } else {
                throw new IllegalStateException("No generated ID");
            }
        }
    }


    public static InteractionItemStaticText loadById(Connection dbc, long id) throws SQLException {
        String sql = "SELECT interaction, ordering, type FROM interaction_static_items_text WHERE id = ?";

        try (PreparedStatement st = dbc.prepareStatement(sql)) {
            st.setLong(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new InteractionItemStaticTextImpl(id, rs.getLong(1), rs.getString(2));
                } else {
                    return null;
                }
            }
        }
    }
}
