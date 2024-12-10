package net.solostudio.vaultcher.commands;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.database.AbstractDatabase;
import net.solostudio.vaultcher.enums.keys.MessageKeys;
import net.solostudio.vaultcher.events.*;
import net.solostudio.vaultcher.managers.VaultcherData;
import net.solostudio.vaultcher.menu.menus.NavigationMenu;
import net.solostudio.vaultcher.utils.MenuUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Objects;

@Command({"vaultcher", "voucher"})
public class CommandVaultcher {
    @DefaultFor({"vaultcher", "voucher"})
    public void defaultCommand(@NotNull CommandSender sender) {
        help(sender);
    }

    @Subcommand("help")
    public void help(@NotNull CommandSender sender) {
        MessageKeys.HELP
                .getMessages()
                .forEach(sender::sendMessage);
    }


    @Subcommand("reload")
    @CommandPermission("vaultcher.reload")
    public void reload(@NotNull CommandSender sender) {
        Vaultcher.getInstance().getLanguage().reload();
        Vaultcher.getInstance().getConfiguration().reload();
        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }

    @Subcommand("menu")
    @CommandPermission("vaultcher.menu")
    public void menu(@NotNull Player player) {
        new NavigationMenu(MenuUtils.getMenuUtils(player)).open();
    }

    @Subcommand("create")
    @CommandPermission("vaultcher.create")
    public void create(@NotNull CommandSender sender, @NotNull String name, int uses, @NotNull String command) {
        AbstractDatabase database = Vaultcher.getDatabase();

        if (database.exists(name)) {
            sender.sendMessage(MessageKeys.ALREADY_EXISTS.getMessage());
            return;
        }

        if (uses < 0) {
            sender.sendMessage(MessageKeys.CANT_BE_NEGATIVE.getMessage());
            return;
        }

        VaultcherData vaultcher = new VaultcherData(name, (command + " ").trim(), uses);
        database.createVaultcher(vaultcher.vaultcherName(), vaultcher.command(), vaultcher.uses());
        sender.sendMessage(MessageKeys.CREATED.getMessage());
        Vaultcher.getInstance().getServer().getPluginManager().callEvent(new VaultcherCreateEvent((Player) sender, name, command, uses));
    }

    @Subcommand("delete")
    @CommandPermission("vaultcher.delete")
    public void delete(@NotNull CommandSender sender, @NotNull String name) {
        AbstractDatabase database = Vaultcher.getDatabase();

        if (!database.exists(name)) {
            sender.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
            return;
        }

        database.deleteVaultcher(name);
        sender.sendMessage(MessageKeys.DELETED.getMessage());
        Vaultcher.getInstance().getServer().getPluginManager().callEvent(new VaultcherDeleteEvent((Player) sender, name));
    }

    @Subcommand("edituse")
    @CommandPermission("vaultcher.edituse")
    public void edituse(@NotNull CommandSender sender, @NotNull String name, int newUse) {
        AbstractDatabase database = Vaultcher.getDatabase();

        if (!database.exists(name)) {
            sender.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
            return;
        }

        if (newUse < 0) {
            sender.sendMessage(MessageKeys.CANT_BE_NEGATIVE.getMessage());
            return;
        }

        Vaultcher.getInstance().getServer().getPluginManager().callEvent(new VaultcherUseEditEvent(name, newUse));
        database.changeUses(name, newUse);
        sender.sendMessage(MessageKeys.EDIT_USES.getMessage());
    }

    @Subcommand("editname")
    @CommandPermission("vaultcher.editname")
    public void editname(@NotNull CommandSender sender, @NotNull String name, @NotNull String newName) {
        AbstractDatabase database = Vaultcher.getDatabase();

        if (!database.exists(name)) {
            sender.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
            return;
        }

        Vaultcher.getInstance().getServer().getPluginManager().callEvent(new VaultcherNameEditEvent(name, newName));
        database.changeName(name, newName);
        sender.sendMessage(MessageKeys.EDIT_NAME.getMessage());
    }

    @Subcommand("editcommand")
    @CommandPermission("vaultcher.editcommand")
    public void editcommand(@NotNull CommandSender sender, @NotNull String name, @NotNull String newCommand) {
        AbstractDatabase database = Vaultcher.getDatabase();

        if (!database.exists(name)) {
            sender.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
            return;
        }

        Vaultcher.getInstance().getServer().getPluginManager().callEvent(new VaultcherCommandEditEvent(name, (newCommand + " ").trim()));
        database.changeCommand(name, (newCommand + " ").trim());
        sender.sendMessage(MessageKeys.EDIT_NAME.getMessage());
    }

    @Subcommand("add")
    @CommandPermission("vaultcher.add")
    public void add(@NotNull CommandSender sender, @NotNull String name, @NotNull String target) {
        AbstractDatabase database = Vaultcher.getDatabase();
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);

        if (!database.exists(name)) {
            sender.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
            return;
        }

        database.giveVaultcher(name, targetPlayer);
        sender.sendMessage(MessageKeys.SUCCESSFUL_ADD.getMessage());
    }

    @Subcommand("redeem")
    @CommandPermission("vaultcher.redeem")
    public void redeem(@NotNull Player player, @NotNull String name) {
        AbstractDatabase database = Vaultcher.getDatabase();

        if (!database.exists(name)) {
            player.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
            return;
        }

        if (database.isUsesZero(name)) {
            player.sendMessage(MessageKeys.USES_ZERO.getMessage());
            return;
        }

        if (!database.isOwned(name, player)) {
            player.sendMessage(MessageKeys.NOT_AN_OWNER.getMessage());
            return;
        }

        database.redeemVaultcher(name, player);
        player.sendMessage(MessageKeys.REDEEMED.getMessage());
    }

    @Subcommand("give")
    @CommandPermission("vaultcher.give")
    public void give(@NotNull Player player, @NotNull String name, @NotNull String target) {
        Player targetPlayer = Bukkit.getPlayer(target);
        AbstractDatabase database = Vaultcher.getDatabase();

        if (!database.exists(name)) {
            player.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
            return;
        }

        if (!database.isOwned(name, player)) {
            player.sendMessage(MessageKeys.NOT_AN_OWNER.getMessage());
            return;
        }

        if (!Objects.requireNonNull(targetPlayer).isOnline()) {
            player.sendMessage(MessageKeys.OFFLINE_PLAYER.getMessage());
            return;
        }

        database.takeVaultcher(name, player.getName(), targetPlayer.getName());
        player.sendMessage(MessageKeys.PLAYER_GIVE.getMessage());
        targetPlayer.sendMessage(MessageKeys.TARGET_GIVE
                .getMessage()
                .replace("{player}", player.getName())
                .replace("{vaultcher}", name));
    }
}
