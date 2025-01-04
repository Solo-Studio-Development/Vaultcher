package net.solostudio.vaultcher.utils;

import net.solostudio.vaultcher.hooks.Webhook;
import net.solostudio.vaultcher.interfaces.PlaceholderProvider;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

public class EventUtils {
    public static void handleEvent(@NotNull String webhookKey, @NotNull PlaceholderProvider event) {
        try {
            Webhook.sendWebhookFromString(webhookKey, event);
        } catch (IOException | URISyntaxException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }

    public static Optional<Player> handleConsoleEvent(@NotNull CommandSender sender) {
        if (sender instanceof Player player) return Optional.of(player);
        return Optional.empty();
    }
}
