package net.solostudio.vaultcher.menu.menus;

import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import net.solostudio.vaultcher.enums.keys.ItemKeys;
import net.solostudio.vaultcher.menu.Menu;
import net.solostudio.vaultcher.managers.MenuController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("all")
public class NavigationMenu extends Menu {

    public NavigationMenu(@NotNull MenuController menuController) {
        super(menuController);
    }

    @Override
    public String getMenuName() {
        return ConfigKeys.NAVIGATION_MENU_TITLE.getString();
    }

    @Override
    public int getSlots() {
        return ConfigKeys.NAVIGATION_MENU_SIZE.getInt();
    }

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
        if (!(event.getWhoClicked() instanceof Player player) || !event.getInventory().equals(inventory)) return;

        event.setCancelled(true);

        int slot = event.getSlot();

        if (slot == ConfigKeys.NAVIGATION_USER_ACCESSIBLE_MENU_SLOT.getInt()) {
            inventory.close();
            new UserAccessibleMenu(MenuController.getMenuUtils(player)).open();
        } else if (slot == ConfigKeys.NAVIGATION_FULL_OVERVIEW_MENU_SLOT.getInt()) {
            if (player.hasPermission("vaultcher.all-menu")) {
                inventory.close();
                new FullOverviewMenu(MenuController.getMenuUtils(player)).open();
            } else inventory.close();
        }
    }

    @Override
    public void setMenuItems() {
        inventory.setItem(ConfigKeys.NAVIGATION_USER_ACCESSIBLE_MENU_SLOT.getInt(), ItemKeys.NAVIGATION_USER_ACCESSIBLE_MENU_ITEM.getItem());
        inventory.setItem(ConfigKeys.NAVIGATION_FULL_OVERVIEW_MENU_SLOT.getInt(), ItemKeys.NAVIGATION_FULL_OVERVIEW_MENU_ITEM.getItem());
        setFillerGlass();
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) close();
    }
}
