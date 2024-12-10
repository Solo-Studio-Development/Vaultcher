package net.solostudio.vaultcher.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class VaultcherUseEditEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String name;
    private final int newUses;

    public VaultcherUseEditEvent(@NotNull final String name, final int newUses) {
        this.name = name;
        this.newUses = newUses;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getName() {
        return this.name;
    }

    public int getNewUses() {
        return this.newUses;
    }
}
