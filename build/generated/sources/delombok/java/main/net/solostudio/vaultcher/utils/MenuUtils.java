package net.solostudio.vaultcher.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MenuUtils {
    @NotNull
    private final Player owner;
    private static final Map<Player, MenuUtils> menuMap = new ConcurrentHashMap<>();

    public static MenuUtils getMenuUtils(@NotNull Player player) {
        return menuMap.computeIfAbsent(player, MenuUtils::new);
    }

    @NotNull
    public Player getOwner() {
        return this.owner;
    }

    public MenuUtils(@NotNull final Player owner) {
        if (owner == null) {
            throw new NullPointerException("owner is marked non-null but is null");
        }
        this.owner = owner;
    }
}
