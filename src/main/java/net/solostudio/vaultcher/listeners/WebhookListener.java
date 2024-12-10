package net.solostudio.vaultcher.listeners;

import net.solostudio.vaultcher.events.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static net.solostudio.vaultcher.utils.EventUtils.handleEvent;

public class WebhookListener implements Listener {
    @EventHandler
    public void onCreate(final VaultcherCreateEvent event) {
        handleEvent("webhook.vaultcher-create-embed", event);
    }

    @EventHandler
    public void onDelete(final VaultcherDeleteEvent event) {
        handleEvent("webhook.vaultcher-delete-embed", event);
    }

    @EventHandler
    public void onNameEdit(final VaultcherNameEditEvent event) {
        handleEvent("webhook.vaultcher-editname-embed", event);
    }

    @EventHandler
    public void onUseEdit(final VaultcherUseEditEvent event) {
        handleEvent("webhook.vaultcher-edituse-embed", event);
    }

    @EventHandler
    public void onCommandEdit(final VaultcherCommandEditEvent event) {
        handleEvent("webhook.vaultcher-editcommand-embed", event);
    }
}
