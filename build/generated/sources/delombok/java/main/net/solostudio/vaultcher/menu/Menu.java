package net.solostudio.vaultcher.menu;

import net.solostudio.vaultcher.enums.keys.ItemKeys;
import net.solostudio.vaultcher.processor.MessageProcessor;
import net.solostudio.vaultcher.utils.MenuUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.stream.IntStream;

@SuppressWarnings("deprecation")
public abstract class Menu implements InventoryHolder {

    protected MenuUtils menuUtils;
    protected Inventory inventory;

    public Menu(@NotNull MenuUtils menuUtils) {
        this.menuUtils = menuUtils;
    }

    public abstract void handleMenu(final InventoryClickEvent event);
    public abstract void setMenuItems();

    public abstract String getMenuName();

    public abstract int getSlots();
    public abstract int getMenuTick();

    public abstract boolean enableFillerGlass();

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void open() {
        inventory = Bukkit.createInventory(this, getSlots(), MessageProcessor.process(getMenuName()));

        this.setMenuItems();

        menuUtils.getOwner().openInventory(inventory);
        MenuUpdater menuUpdater = new MenuUpdater(this);
        menuUpdater.start(getMenuTick());
    }

    public void setFillerGlass() {
        if (!enableFillerGlass()) return;

        IntStream.range(0, getSlots()).forEach(index -> {
            if (inventory.getItem(index) == null) inventory.setItem(index, ItemKeys.FILLER_GLASS_ITEM.getItem());
        });
    }

    public void close() {
        MenuUpdater menuUpdater = new MenuUpdater(this);
        menuUpdater.stop();
        inventory = null;
    }

    public void updateMenuItems() {
        if (inventory != null) {
            setMenuItems();
            menuUtils.getOwner().updateInventory();
        }
    }
}
