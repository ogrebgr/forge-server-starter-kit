package com.bolyartech.forge.server.modules.swoon.data.interaction;

public interface Interaction {
    long getId();

    long getType();

    boolean isDismissable();

    boolean isPostponable();

    boolean isModal();


}
