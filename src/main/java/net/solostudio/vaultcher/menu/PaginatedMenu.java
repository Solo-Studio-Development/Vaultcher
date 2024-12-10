package net.solostudio.vaultcher.menu;

import net.solostudio.vaultcher.utils.MenuUtils;
import org.jetbrains.annotations.NotNull;

public abstract class PaginatedMenu extends Menu {

    public abstract void addMenuBorder();

    protected int page = 0;
    public abstract int getMaxItemsPerPage();

    public PaginatedMenu(@NotNull MenuUtils menuUtils) {
        super(menuUtils);
    }
}
