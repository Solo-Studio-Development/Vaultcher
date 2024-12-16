package net.solostudio.vaultcher.events;

import lombok.Getter;
import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.commands.CommandVaultcher;
import net.solostudio.vaultcher.database.AbstractDatabase;
import net.solostudio.vaultcher.interfaces.PlaceholderProvider;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ReferralActivateEvent extends Event implements PlaceholderProvider {
    private static final HandlerList handlers = new HandlerList();
    private final String creator;
    private final String activator;
    private final String code;

    public ReferralActivateEvent(@NotNull final String creator, @NotNull final String activator, @NotNull final String code) {
        this.creator = creator;
        this.activator = activator;
        this.code = code;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        AbstractDatabase database = Vaultcher.getDatabase();

        placeholders.put("{creator}", database.getReferralCodeOwner(database.getReferralCode(creator)));
        placeholders.put("{activator}", activator);
        placeholders.put("{code}", database.getReferralCode(creator));

        return placeholders;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
