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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import java.util.List;

@SuppressWarnings("all")
public class FullOverviewMenu extends PaginatedMenu {

    public FullOverviewMenu(@NotNull MenuUtils menuUtils) {
        super(menuUtils);
    }

    @Override
    public String getMenuName() {
        return ConfigKeys.FULL_OVERVIEW_MENU_TITLE.getString();
    }

    @Override
    public int getSlots() {
        return ConfigKeys.FULL_OVERVIEW_MENU_SIZE.getInt();
    }

    @Override
    public void addMenuBorder() {
        inventory.setItem(ConfigKeys.FULL_OVERVIEW_BACK_SLOT.getInt(), ItemKeys.FULL_OVERVIEW_BACK_ITEM.getItem());
        inventory.setItem(ConfigKeys.FULL_OVERVIEW_FORWARD_SLOT.getInt(), ItemKeys.FULL_OVERVIEW_FORWARD_ITEM.getItem());
    }

    @Override
    public int getMaxItemsPerPage() {
        return ConfigKeys.FULL_OVERVIEW_MENU_SIZE.getInt() - 2;
    }

    @Override
    public int getMenuTick() {
        return ConfigKeys.FULL_OVERVIEW_MENU_TICK.getInt();
    }

    @Override
    public boolean enableFillerGlass() {
        return ConfigKeys.FULL_OVERVIEW_FILLER_GLASS.getBoolean();
    }

    @Override
    public void setMenuItems() {
        List<VaultcherData> vaultchers = Vaultcher.getDatabase().getEveryVaultcher();
        int startIndex = page * getMaxItemsPerPage();
        int endIndex = Math.min(startIndex + getMaxItemsPerPage(), vaultchers.size());

        inventory.clear();
        addMenuBorder();

        vaultchers.subList(startIndex, endIndex)
                .stream()
                .map(this::createVaultcherItem)
                .forEach(inventory::addItem);
    }

    @Override
    public void handleMenu(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getInventory().equals(inventory)) return;

        event.setCancelled(true);

        List<VaultcherData> vaultchers = Vaultcher.getDatabase().getVaultchers(player);
        int clickedSlot = event.getSlot();

        if (clickedSlot == ConfigKeys.USER_ACCESSIBLE_FORWARD_SLOT.getInt()) handlePageChange(player, vaultchers.size(), true);
        else if (clickedSlot == ConfigKeys.USER_ACCESSIBLE_BACK_SLOT.getInt()) handlePageChange(player, vaultchers.size(), false);
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) close();
    }

    private ItemStack createVaultcherItem(@NotNull VaultcherData vaultcherData) {
        ItemStack itemStack = ItemKeys.VAULTCHER_ITEM.getItem();
        ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(meta.getDisplayName().replace("{name}", vaultcherData.vaultcherName()));
            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    private void handlePageChange(Player player, int totalItems, boolean isForward) {
        int totalPages = (int) Math.ceil((double) totalItems / getMaxItemsPerPage());
        int newPage = page + (isForward ? 1 : -1);

        if (newPage < 0 || newPage >= totalPages) {
            player.sendMessage(isForward ? MessageKeys.LAST_PAGE.getMessage() : MessageKeys.FIRST_PAGE.getMessage());
            return;
        }

        page = newPage;
        super.open();
    }
}