package net.solostudio.vaultcher.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class VaultcherDeleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String name;

    public VaultcherDeleteEvent(@NotNull final Player player, @NotNull final String name) {
        this.player = player;
        this.name = name;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getName() {
        return this.name;
    }
}
