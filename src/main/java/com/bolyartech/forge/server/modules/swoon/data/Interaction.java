package com.bolyartech.forge.server.modules.swoon.data;

public interface Interaction {
    long getId();

    long getType();

    boolean isDismissable();

    boolean isPostponable();

    boolean isModal();
}
