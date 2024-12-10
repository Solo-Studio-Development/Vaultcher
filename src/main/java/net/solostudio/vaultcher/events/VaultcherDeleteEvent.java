package net.solostudio.vaultcher.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class VaultcherDeleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final String name;

    public VaultcherDeleteEvent(@NotNull final Player player, @NotNull final String name) {
        this.player = player;
        this.name = name;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
