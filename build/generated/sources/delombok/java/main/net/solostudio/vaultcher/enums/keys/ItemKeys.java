package net.solostudio.vaultcher.enums.keys;

import net.solostudio.vaultcher.item.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum ItemKeys {
    CLAIMED_BACK_ITEM("claimed-menu.back-item"),
    FULL_OVERVIEW_BACK_ITEM("full-overview-menu.back-item"),
    FULL_OVERVIEW_FORWARD_ITEM("full-overview-menu.forward-item"),
    USER_ACCESSIBLE_FORWARD_ITEM("user-accessible-menu.forward-item"),
    USER_ACCESSIBLE_BACK_ITEM("user-accessible-menu.back-item"),
    NAVIGATION_FULL_OVERVIEW_MENU_ITEM("navigation-menu.full-overview-menu-item"),
    NAVIGATION_USER_ACCESSIBLE_MENU_ITEM("navigation-menu.user-accessible-menu-item"),
    VAULTCHER_ITEM("vaultcher-item"),
    FILLER_GLASS_ITEM("filler-glass-item"),
    CLAIMED_FORWARD_ITEM("claimed-menu.forward-item");

    private final String path;

    ItemKeys(@NotNull final String path) {
        this.path = path;
    }

    public ItemStack getItem() {
        return ItemFactory.createItemFromString(path);
    };
}
