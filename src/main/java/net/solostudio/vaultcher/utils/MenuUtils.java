package net.solostudio.vaultcher.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@RequiredArgsConstructor
public final class MenuUtils {
    private final @NotNull Player owner;
    private static final Map<Player, MenuUtils> menuMap = new ConcurrentHashMap<>();

    public static MenuUtils getMenuUtils(@NotNull Player player) {
        return menuMap.computeIfAbsent(player, MenuUtils::new);
    }
}

