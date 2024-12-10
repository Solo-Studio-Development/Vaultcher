package net.solostudio.vaultcher.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class VaultcherCommandEditEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String name;
    private final String newCommand;

    public VaultcherCommandEditEvent(@NotNull final String name, @NotNull final String newCommand) {
        this.name = name;
        this.newCommand = newCommand;
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

    public String getNewCommand() {
        return this.newCommand;
    }
}
