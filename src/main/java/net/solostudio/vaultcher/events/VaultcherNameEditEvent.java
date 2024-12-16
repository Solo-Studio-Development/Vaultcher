package net.solostudio.vaultcher.events;

import lombok.Getter;
import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.interfaces.PlaceholderProvider;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public class VaultcherNameEditEvent extends Event implements PlaceholderProvider {
    private static final HandlerList handlers = new HandlerList();
    private final String name;
    private final String newName;

    public VaultcherNameEditEvent(@NotNull final String name, @NotNull final String newName) {
        this.name = name;
        this.newName = newName;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{old}", Vaultcher.getDatabase().getName(getName()));
        placeholders.put("{new}", getNewName());
        placeholders.put("{vaultcher}", getName());

        return placeholders;
    }
}
