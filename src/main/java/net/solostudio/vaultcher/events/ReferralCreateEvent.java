package net.solostudio.vaultcher.events;

import lombok.Getter;
import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.interfaces.PlaceholderProvider;
import net.solostudio.vaultcher.interfaces.VaultcherDatabase;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ReferralCreateEvent extends Event implements PlaceholderProvider {
    private static final HandlerList handlers = new HandlerList();
    private final String creator;
    private final String code;

    public ReferralCreateEvent(@NotNull final String creator, @NotNull final String code) {
        this.creator = creator;
        this.code = code;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        VaultcherDatabase vaultcherDatabase = Vaultcher.getDatabase();

        placeholders.put("{creator}", vaultcherDatabase.getReferralCodeOwner(vaultcherDatabase.getReferralCode(creator)));
        placeholders.put("{code}", vaultcherDatabase.getReferralCode(creator));

        return placeholders;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}