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
public class VaultcherUseEditEvent extends Event implements PlaceholderProvider {
    private static final HandlerList handlers = new HandlerList();
    private final String name;
    private final int newUses;

    public VaultcherUseEditEvent(@NotNull final String name, final int newUses) {
        this.name = name;
        this.newUses = newUses;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{old}", String.valueOf(Vaultcher.getDatabase().getUses(getName())));
        placeholders.put("{new}", String.valueOf(getNewUses()));
        placeholders.put("{vaultcher}", getName());

        return placeholders;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
