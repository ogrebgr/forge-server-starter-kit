package com.bolyartech.forge.server.modules.swoon.data.interaction_item;

public interface InteractionItem {
    long getId();

    long getInteraction();

    long getOrdering();

    long getType();


    enum TYPE_DB_IDS {
        STATIC(1),
        ACTION(2),
        VALUE(3);

        private final long mId;

        TYPE_DB_IDS(long id) {
            mId = id;
        }


        public long getId() {
            return mId;
        }
    }
}
