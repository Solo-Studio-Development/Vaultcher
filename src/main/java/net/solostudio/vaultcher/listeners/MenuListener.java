package net.solostudio.vaultcher.listeners;

import net.solostudio.vaultcher.menu.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {
    @EventHandler
    public void onClick(final InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof Menu menu) {
            event.setCancelled(true);
            menu.handleMenu(event);
        }
    }
}
