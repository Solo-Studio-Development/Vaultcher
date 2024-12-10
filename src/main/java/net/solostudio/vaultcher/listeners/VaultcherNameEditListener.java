package net.solostudio.vaultcher.listeners;

import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import net.solostudio.vaultcher.events.VaultcherNameEditEvent;
import net.solostudio.vaultcher.hook.Webhook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

public class VaultcherNameEditListener implements Listener {
    @EventHandler
    public void onEditName(final VaultcherNameEditEvent event) throws IOException, NoSuchFieldException, IllegalAccessException {
        Webhook.sendWebhook(Webhook.replacePlaceholdersCodeEditName(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_DESCRIPTION.getString(), event),
                ConfigKeys.WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_COLOR.getString(),
                Webhook.replacePlaceholdersCodeEditName(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_AUTHOR_NAME.getString(), event),
                Webhook.replacePlaceholdersCodeEditName(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_AUTHOR_URL.getString(), event),
                Webhook.replacePlaceholdersCodeEditName(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_AUTHOR_ICON.getString(), event),
                Webhook.replacePlaceholdersCodeEditName(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_FOOTER_TEXT.getString(), event),
                Webhook.replacePlaceholdersCodeEditName(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_FOOTER_ICON.getString(), event),
                Webhook.replacePlaceholdersCodeEditName(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_THUMBNAIL.getString(), event),
                Webhook.replacePlaceholdersCodeEditName(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_TITLE.getString(), event),
                Webhook.replacePlaceholdersCodeEditName(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_NAME_EMBED_IMAGE.getString(), event));
    }
}
