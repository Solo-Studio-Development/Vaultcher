package net.solostudio.vaultcher.listeners;

import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import net.solostudio.vaultcher.events.VaultcherDeleteEvent;
import net.solostudio.vaultcher.hook.Webhook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

public class VaultcherDeleteListener implements Listener {
    @EventHandler
    public void onCreate(final VaultcherDeleteEvent event) throws IOException, NoSuchFieldException, IllegalAccessException {
        Webhook.sendWebhook(Webhook.replacePlaceholdersCodeDelete(ConfigKeys.WEBHOOK_VAULTCHER_DELETE_EMBED_DESCRIPTION.getString(), event),
                ConfigKeys.WEBHOOK_VAULTCHER_DELETE_EMBED_COLOR.getString(),
                Webhook.replacePlaceholdersCodeDelete(ConfigKeys.WEBHOOK_VAULTCHER_DELETE_EMBED_AUTHOR_NAME.getString(), event),
                Webhook.replacePlaceholdersCodeDelete(ConfigKeys.WEBHOOK_VAULTCHER_DELETE_EMBED_AUTHOR_URL.getString(), event),
                Webhook.replacePlaceholdersCodeDelete(ConfigKeys.WEBHOOK_VAULTCHER_DELETE_EMBED_AUTHOR_ICON.getString(), event),
                Webhook.replacePlaceholdersCodeDelete(ConfigKeys.WEBHOOK_VAULTCHER_DELETE_EMBED_FOOTER_TEXT.getString(), event),
                Webhook.replacePlaceholdersCodeDelete(ConfigKeys.WEBHOOK_VAULTCHER_DELETE_EMBED_FOOTER_ICON.getString(), event),
                Webhook.replacePlaceholdersCodeDelete(ConfigKeys.WEBHOOK_VAULTCHER_DELETE_EMBED_THUMBNAIL.getString(), event),
                Webhook.replacePlaceholdersCodeDelete(ConfigKeys.WEBHOOK_VAULTCHER_DELETE_EMBED_TITLE.getString(), event),
                Webhook.replacePlaceholdersCodeDelete(ConfigKeys.WEBHOOK_VAULTCHER_DELETE_EMBED_IMAGE.getString(), event));
    }
}
