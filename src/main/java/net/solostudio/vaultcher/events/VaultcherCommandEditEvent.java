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
public class VaultcherCommandEditEvent extends Event implements PlaceholderProvider {
    private static final HandlerList handlers = new HandlerList();
    private final String name;
    private final String newCommand;

    public VaultcherCommandEditEvent(@NotNull final String name, @NotNull final String newCommand) {
        this.name = name;
        this.newCommand = newCommand;
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

        placeholders.put("{old}", Vaultcher.getDatabase().getCommand(getName()));
        placeholders.put("{new}", getNewCommand());
        placeholders.put("{vaultcher}", getName());

        return placeholders;
    }
}

