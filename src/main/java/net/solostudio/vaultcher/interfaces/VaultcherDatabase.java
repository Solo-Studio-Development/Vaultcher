package net.solostudio.vaultcher.interfaces;

import net.solostudio.vaultcher.managers.VaultcherData;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface VaultcherDatabase {
    boolean isConnected();

    void disconnect();

    void createTable();

    int countVaultchers();

    void createPlayer(@NotNull String name);

    void createReferralCode(@NotNull String name, @NotNull String referralCode);

    boolean doesPlayerExists(@NotNull String name);

    String generateSafeCode();

    boolean doesReferralCodeExist(String code);

    void activateReferral(@NotNull String name);

    void incrementActivators(@NotNull String referralCode);

    int getActivators(@NotNull String referralCode);

    int getActivatorsFromPlayer(@NotNull String name);

    boolean isReferralActivated(@NotNull String name);

    String getReferralCode(@NotNull String name);

    String getReferralCodeOwner(@NotNull String referralCode);

    void addCommand(@NotNull String vaultcherName, @NotNull String newCommand);

    void createVaultcher(@NotNull String name, @NotNull String command, int uses);

    void redeemVaultcher(@NotNull String vaultcherName, @NotNull OfflinePlayer player);

    boolean exists(@NotNull String name);

    void giveVaultcher(@NotNull String code, @NotNull OfflinePlayer player);

    boolean isOwned(@NotNull String code, @NotNull OfflinePlayer player);

    boolean isUsesZero(@NotNull String code);

    int getUses(@NotNull String code);

    String getCommand(@NotNull String code);

    String getName(@NotNull String code);

    void takeVaultcher(@NotNull String vaultcher, @NotNull String ownerToRemove);

    void decrementUses(@NotNull String vaultcherName);

    void deleteVaultcher(@NotNull String code);

    void changeName(@NotNull String oldName, @NotNull String newName);

    void changeCommand(@NotNull String name, @NotNull String newCommand);

    void changeUses(@NotNull String name, int newUses);

    List<VaultcherData> getVaultchers(@NotNull OfflinePlayer player);

    List<String> getEveryPlayerInDatabase();

    List<VaultcherData> getEveryVaultcher();

    void reconnect();
}