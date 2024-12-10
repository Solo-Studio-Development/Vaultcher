package net.solostudio.vaultcher.listeners;

import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import net.solostudio.vaultcher.events.VaultcherUseEditEvent;
import net.solostudio.vaultcher.hook.Webhook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

public class VaultcherUseEditListener implements Listener {
    @EventHandler
    public void onEditUse(final VaultcherUseEditEvent event) throws IOException, NoSuchFieldException, IllegalAccessException {
        Webhook.sendWebhook(Webhook.replacePlaceholdersCodeEditUse(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_USE_EMBED_DESCRIPTION.getString(), event),
                ConfigKeys.WEBHOOK_VAULTCHER_EDIT_USE_EMBED_COLOR.getString(),
                Webhook.replacePlaceholdersCodeEditUse(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_USE_EMBED_AUTHOR_NAME.getString(), event),
                Webhook.replacePlaceholdersCodeEditUse(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_USE_EMBED_AUTHOR_URL.getString(), event),
                Webhook.replacePlaceholdersCodeEditUse(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_USE_EMBED_AUTHOR_ICON.getString(), event),
                Webhook.replacePlaceholdersCodeEditUse(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_USE_EMBED_FOOTER_TEXT.getString(), event),
                Webhook.replacePlaceholdersCodeEditUse(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_USE_EMBED_FOOTER_ICON.getString(), event),
                Webhook.replacePlaceholdersCodeEditUse(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_USE_EMBED_THUMBNAIL.getString(), event),
                Webhook.replacePlaceholdersCodeEditUse(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_USE_EMBED_TITLE.getString(), event),
                Webhook.replacePlaceholdersCodeEditUse(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_USE_EMBED_IMAGE.getString(), event));
    }
}
