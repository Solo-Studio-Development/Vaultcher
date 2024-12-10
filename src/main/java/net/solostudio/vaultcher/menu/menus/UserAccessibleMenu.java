package net.solostudio.vaultcher.menu.menus;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import net.solostudio.vaultcher.enums.keys.ItemKeys;
import net.solostudio.vaultcher.enums.keys.MessageKeys;
import net.solostudio.vaultcher.managers.VaultcherData;
import net.solostudio.vaultcher.menu.PaginatedMenu;
import net.solostudio.vaultcher.utils.MenuUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("deprecation")
public class UserAccessibleMenu extends PaginatedMenu implements Listener {
    public UserAccessibleMenu(@NotNull MenuUtils menuUtils) {
        super(menuUtils);
    }

    @Override
    public String getMenuName() {
        return ConfigKeys.USER_ACCESSIBLE_MENU_TITLE.getString();
    }

    @Override
    public int getSlots() {
        return ConfigKeys.USER_ACCESSIBLE_MENU_SIZE.getInt();
    }

    @Override
    public void addMenuBorder() {
        inventory.setItem(ConfigKeys.USER_ACCESSIBLE_BACK_SLOT.getInt(), ItemKeys.USER_ACCESSIBLE_BACK_ITEM.getItem());
        inventory.setItem(ConfigKeys.USER_ACCESSIBLE_FORWARD_SLOT.getInt(), ItemKeys.USER_ACCESSIBLE_FORWARD_ITEM.getItem());
    }

    @Override
    public int getMaxItemsPerPage() {
        return ConfigKeys.USER_ACCESSIBLE_MENU_SIZE.getInt() - 2;
    }

    @Override
    public int getMenuTick() {
        return ConfigKeys.USER_ACCESSIBLE_MENU_TICK.getInt();
    }

    @Override
    public boolean enableFillerGlass() {
        return ConfigKeys.USER_ACCESSIBLE_FILLER_GLASS.getBoolean();
    }


    @Override
    public void setMenuItems() {
        List<VaultcherData> vaultchers = Vaultcher.getDatabase().getVaultchers(menuUtils.getOwner());

        inventory.clear();
        addMenuBorder();

        int startIndex = page * getMaxItemsPerPage();
        int endIndex = Math.min(startIndex + getMaxItemsPerPage(), vaultchers.size());

        IntStream.range(startIndex, endIndex).forEach(index -> inventory.addItem(createCodeItem(vaultchers.get(index))));
    }

    @Override
    public void handleMenu(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getInventory().equals(inventory)) return;

        event.setCancelled(true);

        List<VaultcherData> vaultchers = Vaultcher.getDatabase().getVaultchers(player);

        if (event.getSlot() == ConfigKeys.USER_ACCESSIBLE_FORWARD_SLOT.getInt()) {
            int nextPageIndex = page + 1;
            int totalPages = (int) Math.ceil((double) vaultchers.size() / getMaxItemsPerPage());

            if (nextPageIndex >= totalPages) {
                player.sendMessage(MessageKeys.LAST_PAGE.getMessage());
                return;
            } else {
                page++;
                super.open();
            }
        }

        if (event.getSlot() == ConfigKeys.USER_ACCESSIBLE_BACK_SLOT.getInt()) {
            if (page == 0) {
                player.sendMessage(MessageKeys.FIRST_PAGE.getMessage());
                return;
            } else {
                page--;
                super.open();
            }
        }

        Vaultcher.getDatabase().redeemVaultcher(vaultchers.get(event.getSlot()).vaultcherName(), player);
        inventory.close();
        player.sendMessage(MessageKeys.REDEEMED.getMessage());
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) close();
    }

    private static ItemStack createCodeItem(@NotNull VaultcherData vaultcher) {
        ItemStack itemStack = ItemKeys.VAULTCHER_ITEM.getItem();
        ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            String displayName = meta.getDisplayName()
                    .replace("{name}", vaultcher.vaultcherName());
            meta.setDisplayName(displayName);
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
