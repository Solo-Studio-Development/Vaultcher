package net.solostudio.vaultcher.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class VaultcherCreateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String name;
    private final String command;
    private final int uses;

    public VaultcherCreateEvent(@NotNull final Player player, @NotNull final String name, @NotNull final String command, final int uses) {
        this.player = player;
        this.name = name;
        this.command = command;
        this.uses = uses;
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

    public String getCommand() {
        return this.command;
    }

    public int getUses() {
        return this.uses;
    }
}
