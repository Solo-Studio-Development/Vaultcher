package net.solostudio.vaultcher.database;

import net.solostudio.vaultcher.managers.VaultcherData;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractDatabase {
    public abstract boolean isConnected();

    public abstract void disconnect();

    public abstract void createVaultcher(@NotNull String name, @NotNull String cmd, int uses);

    public abstract boolean exists(@NotNull String name);

    public abstract void redeemVaultcher(@NotNull String name, @NotNull OfflinePlayer player);

    public abstract void giveVaultcher(@NotNull String code, @NotNull OfflinePlayer player);

    public abstract boolean isOwned(@NotNull String code, @NotNull OfflinePlayer player);

    public abstract boolean isUsesZero(@NotNull String code);

    public abstract int getUses(@NotNull String code);

    public abstract String getCommand(@NotNull String code);

    public abstract String getName(@NotNull String code);

    public abstract void takeVaultcher(@NotNull String code, @NotNull String oldOwner, @NotNull String newOwner);

    public abstract void deleteVaultcher(@NotNull String code);

    public abstract void changeName(@NotNull String oldName, @NotNull String newName);

    public abstract void changeCommand(@NotNull String name, @NotNull String newCommand);

    public abstract void changeUses(@NotNull String name, int newUses);

    public abstract List<VaultcherData> getVaultchers(@NotNull OfflinePlayer player);

    public abstract List<VaultcherData> getEveryVaultcher();

    public abstract void reconnect();
}