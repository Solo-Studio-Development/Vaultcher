package net.solostudio.vaultcher.utils;

import net.solostudio.vaultcher.hook.Webhook;
import net.solostudio.vaultcher.interfaces.PlaceholderProvider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;

public class EventUtils {
    public static void handleEvent(@NotNull String webhookKey, @NotNull PlaceholderProvider event) {
        try {
            Webhook.sendWebhookFromString(webhookKey, event);
        } catch (IOException | URISyntaxException exception) {
            LoggerUtils.error(exception.getMessage());
        }
    }
}
