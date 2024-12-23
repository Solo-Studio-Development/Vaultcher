package net.solostudio.vaultcher.database;

import net.solostudio.vaultcher.managers.VaultcherData;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class AbstractDatabase {
    public abstract boolean isConnected();

    public abstract void disconnect();

    public abstract void createTable();

    public abstract void createPlayer(@NotNull String name);

    public abstract void createReferralCode(@NotNull String name, @NotNull String referralCode);

    public abstract boolean doesPlayerExists(@NotNull String name);

    public abstract String generateSafeCode();

    public abstract boolean doesReferralCodeExist(String code);

    public abstract void activateReferral(@NotNull String name);

    public abstract void incrementActivators(@NotNull String referralCode);

    public abstract int getActivators(@NotNull String referralCode);

    public abstract int getActivatorsFromPlayer(@NotNull String name);

    public abstract boolean isReferralActivated(@NotNull String name);

    public abstract String getReferralCode(@NotNull String name);

    public abstract String getReferralCodeOwner(@NotNull String referralCode);

    public abstract void createVaultcher(@NotNull String name, @NotNull String command, int uses);

    public abstract void redeemVaultcher(@NotNull String vaultcherName, @NotNull OfflinePlayer player);

    public abstract boolean exists(@NotNull String name);

    public abstract void giveVaultcher(@NotNull String code, @NotNull OfflinePlayer player);

    public abstract boolean isOwned(@NotNull String code, @NotNull OfflinePlayer player);

    public abstract boolean isUsesZero(@NotNull String code);

    public abstract int getUses(@NotNull String code);

    public abstract String getCommand(@NotNull String code);

    public abstract String getName(@NotNull String code);

    public abstract void takeVaultcher(@NotNull String vaultcher, @NotNull String ownerToRemove);

    public abstract void decrementUses(@NotNull String vaultcherName);

    public abstract void deleteVaultcher(@NotNull String code);

    public abstract void changeName(@NotNull String oldName, @NotNull String newName);

    public abstract void changeCommand(@NotNull String name, @NotNull String newCommand);

    public abstract void changeUses(@NotNull String name, int newUses);

    public abstract List<VaultcherData> getVaultchers(@NotNull OfflinePlayer player);

    public abstract List<String> getEveryPlayerInDatabase();

    public abstract List<VaultcherData> getEveryVaultcher();

    public abstract void reconnect();
}