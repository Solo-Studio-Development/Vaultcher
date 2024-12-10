package net.solostudio.vaultcher.menu.menus;

import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import net.solostudio.vaultcher.enums.keys.ItemKeys;
import net.solostudio.vaultcher.menu.Menu;
import net.solostudio.vaultcher.utils.ConfigUtils;
import net.solostudio.vaultcher.utils.MenuUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

public class NavigationMenu extends Menu implements Listener {
    public NavigationMenu(@NotNull MenuUtils menuUtils) {
        super(menuUtils);
    }

    @Override
    public String getMenuName() { return ConfigKeys.NAVIGATION_MENU_TITLE.getString(); }

    @Override
    public int getSlots() { return ConfigKeys.NAVIGATION_MENU_SIZE.getInt(); }

    @Override
    public boolean enableFillerGlass() {
        return ConfigKeys.NAVIGATION_MENU_FILLER_GLASS.getBoolean();
    }

    @Override
    public int getMenuTick() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void handleMenu(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getInventory().equals(inventory)) return;

        event.setCancelled(true);

        if (event.getSlot() == ConfigKeys.NAVIGATION_USER_ACCESSIBLE_MENU_SLOT.getInt()) {
            inventory.close();
            new UserAccessibleMenu(MenuUtils.getMenuUtils(player)).open();
        }

        if (event.getSlot() == ConfigKeys.NAVIGATION_FULL_OVERVIEW_MENU_SLOT.getInt()) {
            if (!player.hasPermission("vaultcher.all-menu")) {
                inventory.close();
                return;
            }

            inventory.close();
            new FullOverviewMenu(MenuUtils.getMenuUtils(player)).open();
        }
    }

    @Override
    public void setMenuItems() {
        inventory.setItem(ConfigKeys.NAVIGATION_USER_ACCESSIBLE_MENU_SLOT.getInt(), ItemKeys.NAVIGATION_USER_ACCESSIBLE_MENU_ITEM.getItem());
        inventory.setItem(ConfigKeys.NAVIGATION_FULL_OVERVIEW_MENU_SLOT.getInt(), ItemKeys.NAVIGATION_USER_ACCESSIBLE_MENU_ITEM.getItem());
        setFillerGlass();
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) close();
    }
}
