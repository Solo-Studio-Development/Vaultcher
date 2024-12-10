package net.solostudio.vaultcher.listeners;

import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import net.solostudio.vaultcher.events.VaultcherCommandEditEvent;
import net.solostudio.vaultcher.hook.Webhook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

public class VaultcherCommandEditListener implements Listener {
    @EventHandler
    public void onEditCommand(final VaultcherCommandEditEvent event) throws IOException, NoSuchFieldException, IllegalAccessException {
        Webhook.sendWebhook(Webhook.replacePlaceholdersCodeEditCommand(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_DESCRIPTION.getString(), event),
                ConfigKeys.WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_COLOR.getString(),
                Webhook.replacePlaceholdersCodeEditCommand(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_AUTHOR_NAME.getString(), event),
                Webhook.replacePlaceholdersCodeEditCommand(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_AUTHOR_URL.getString(), event),
                Webhook.replacePlaceholdersCodeEditCommand(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_AUTHOR_ICON.getString(), event),
                Webhook.replacePlaceholdersCodeEditCommand(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_FOOTER_TEXT.getString(), event),
                Webhook.replacePlaceholdersCodeEditCommand(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_FOOTER_ICON.getString(), event),
                Webhook.replacePlaceholdersCodeEditCommand(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_THUMBNAIL.getString(), event),
                Webhook.replacePlaceholdersCodeEditCommand(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_TITLE.getString(), event),
                Webhook.replacePlaceholdersCodeEditCommand(ConfigKeys.WEBHOOK_VAULTCHER_EDIT_COMMAND_EMBED_IMAGE.getString(), event));
    }
}
