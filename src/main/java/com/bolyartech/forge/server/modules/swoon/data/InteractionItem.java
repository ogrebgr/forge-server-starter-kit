package com.bolyartech.forge.server.modules.swoon.data;

import java.sql.Connection;

public class InteractionItem {
    private long mId;
    private long mInteraction;
    private long mOrdering;
    private long mType;


    public InteractionItem() {

    }


    public InteractionItem(long id, long interaction, long ordering, long type) {
        mId = id;
        mInteraction = interaction;
        mOrdering = ordering;
        mType = type;
    }


    public long getId() {
        return mId;
    }


    public long getInteraction() {
        return mInteraction;
    }


    public long getOrdering() {
        return mOrdering;
    }


    public long getType() {
        return mType;
    }


    public long save(Connection dbc) {
        if (mId == 0) {
            return insert(dbc);
        } else {

        }
    }
}
