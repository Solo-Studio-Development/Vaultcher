package net.solostudio.vaultcher.events;

import lombok.Getter;
import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.interfaces.PlaceholderProvider;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
public class VaultcherDeleteEvent extends Event implements PlaceholderProvider {
    private static final HandlerList handlers = new HandlerList();
    private final @Nullable Player player;
    private final String name;

    public VaultcherDeleteEvent(@Nullable final Player player, @NotNull final String name) {
        this.player = player;
        this.name = name;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{player}", (player != null) ? player.getName() : "CONSOLE");
        placeholders.put("{vaultcher}", getName());

        return placeholders;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
