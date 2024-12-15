package net.solostudio.vaultcher.listeners;

import net.solostudio.vaultcher.Vaultcher;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DatabaseListener implements Listener {
    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        Vaultcher.getDatabase().createPlayer(event.getPlayer().getName());
    }
}
