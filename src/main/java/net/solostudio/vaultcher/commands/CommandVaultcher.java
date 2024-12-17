package net.solostudio.vaultcher.commands;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.annotations.VaultcherCommand;
import net.solostudio.vaultcher.database.AbstractDatabase;
import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import net.solostudio.vaultcher.enums.keys.MessageKeys;
import net.solostudio.vaultcher.events.*;
import net.solostudio.vaultcher.managers.VaultcherData;
import net.solostudio.vaultcher.menu.menus.NavigationMenu;
import net.solostudio.vaultcher.utils.EventUtils;
import net.solostudio.vaultcher.managers.MenuController;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Command({"vaultcher", "voucher"})
public class CommandVaultcher {
    @CommandPlaceholder
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
    @Description("Reloads the plugin.")
    public void reload(@NotNull CommandSender sender) {
        Vaultcher.getInstance().getLanguage().reload();
        Vaultcher.getInstance().getConfiguration().reload();
        sender.sendMessage(MessageKeys.RELOAD.getMessage());
    }

    @Subcommand("menu")
    @CommandPermission("vaultcher.menu")
    @Description("Opens the vaultcher menu.")
    public void menu(@NotNull Player player) {
        new NavigationMenu(MenuController.getMenuUtils(player)).open();
    }

    @Subcommand("create")
    @CommandPermission("vaultcher.create")
    @Usage("/vaultcher create name: <name> uses: <uses> command: <command>")
    @Description("Creates a new vaultcher.")
    public void create(@NotNull CommandSender sender, @NotNull @VaultcherCommand String input) {
        AbstractDatabase database = Vaultcher.getDatabase();
        input = input.trim();
        Pattern pattern = Pattern.compile("name:\\s*(.*?)\\s+uses:\\s*(\\d+)\\s+command:\\s*(.*)");
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            String name = matcher.group(1).trim();
            int uses;

            try {
                uses = Integer.parseInt(matcher.group(2).trim());
            } catch (NumberFormatException exception) {
                sender.sendMessage(MessageKeys.INVALID_NUMBER.getMessage());
                return;
            }
            String command = matcher.group(3).trim();

            if (database.exists(name)) {
                sender.sendMessage(MessageKeys.ALREADY_EXISTS.getMessage());
                return;
            }

            if (uses < 0) {
                sender.sendMessage(MessageKeys.CANT_BE_NEGATIVE.getMessage());
                return;
            }

            VaultcherData vaultcher = new VaultcherData(name, command, uses);
            database.createVaultcher(vaultcher.vaultcherName(), vaultcher.command(), vaultcher.uses());
            sender.sendMessage(MessageKeys.CREATED.getMessage());
            Vaultcher.getInstance().getServer().getPluginManager().callEvent(
                    new VaultcherCreateEvent(EventUtils.handleConsoleEvent(sender).orElse(null), name, command, uses)
            );
        } else sender.sendMessage(MessageKeys.CREATE_FORMAT.getMessage());
    }

    @Subcommand("delete")
    @CommandPermission("vaultcher.delete")
    @Usage("vaultcher delete name: <name>")
    @Description("Deletes the vaultcher.")
    public void delete(@NotNull CommandSender sender, @NotNull @VaultcherCommand String input) {
        AbstractDatabase database = Vaultcher.getDatabase();
        Pattern pattern = Pattern.compile("name:\\s*(.*)");
        Matcher matcher = pattern.matcher(input.trim());

        if (matcher.matches()) {
            String name = matcher.group(1).trim();

            if (!database.exists(name)) {
                sender.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
                return;
            }

            database.deleteVaultcher(name);
            sender.sendMessage(MessageKeys.DELETED.getMessage());
            Vaultcher.getInstance().getServer().getPluginManager().callEvent(new VaultcherDeleteEvent(EventUtils.handleConsoleEvent(sender).orElse(null), name));
        } else sender.sendMessage(MessageKeys.DELETE_FORMAT.getMessage());
    }

    @Subcommand("edituse")
    @CommandPermission("vaultcher.edituse")
    @Usage("/vaultcher edituse name: <name> new: <new use>")
    @Description("Edits the uses of the vaultcher.")
    public void edituse(@NotNull CommandSender sender, @NotNull @VaultcherCommand String input) {
        AbstractDatabase database = Vaultcher.getDatabase();
        Pattern pattern = Pattern.compile("name:\\s*(.*?)\\s+new:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(input.trim());

        if (matcher.matches()) {
            String name = matcher.group(1).trim();
            int newUse;
            try {
                newUse = Integer.parseInt(matcher.group(2).trim());
            } catch (NumberFormatException e) {
                sender.sendMessage("Hiba: A 'newUse' paraméter nem érvényes szám.");
                return;
            }

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
        } else sender.sendMessage(MessageKeys.EDITUSE_FORMAT.getMessage());
    }

    @Subcommand("editname")
    @CommandPermission("vaultcher.editname")
    @Usage("/vaultcher editname name: <name> new: <new name>")
    @Description("Edits the name of the vaultcher.")
    public void editname(@NotNull CommandSender sender, @NotNull @VaultcherCommand String input) {
        AbstractDatabase database = Vaultcher.getDatabase();
        Pattern pattern = Pattern.compile("name:\\s*(.*?)\\s+new:\\s*(.*)");
        Matcher matcher = pattern.matcher(input.trim());

        if (matcher.matches()) {
            String name = matcher.group(1).trim();
            String newName = matcher.group(2).trim();

            if (!database.exists(name)) {
                sender.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
                return;
            }

            Vaultcher.getInstance().getServer().getPluginManager().callEvent(new VaultcherNameEditEvent(name, newName));
            database.changeName(name, newName);
            sender.sendMessage(MessageKeys.EDIT_NAME.getMessage());
        } else sender.sendMessage(MessageKeys.EDITNAME_FORMAT.getMessage());
    }

    @Subcommand("editcommand")
    @CommandPermission("vaultcher.editcommand")
    @Usage("/vaultcher editcommand name: <name> new: <new command>")
    @Description("Edits the command of the vaultcher.")
    public void editcommand(@NotNull CommandSender sender, @NotNull @VaultcherCommand String input) {
        AbstractDatabase database = Vaultcher.getDatabase();
        Pattern pattern = Pattern.compile("name:\\s*(.*?)\\s+new:\\s*(.*)");
        Matcher matcher = pattern.matcher(input.trim());

        if (matcher.matches()) {
            String name = matcher.group(1).trim();
            String newCommand = matcher.group(2).trim();

            if (!database.exists(name)) {
                sender.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
                return;
            }

            Vaultcher.getInstance().getServer().getPluginManager().callEvent(new VaultcherCommandEditEvent(name, newCommand));
            database.changeCommand(name, newCommand);
            sender.sendMessage(MessageKeys.EDIT_CMD.getMessage());
        } else sender.sendMessage(MessageKeys.EDITCOMMAND_FORMAT.getMessage());
    }

    @Subcommand("add")
    @CommandPermission("vaultcher.add")
    @Usage("/vaultcher add name: <name> target: <target>")
    @Description("Adds a permission to the vaultcher.")
    public void add(@NotNull CommandSender sender, @NotNull @VaultcherCommand String input) {
        AbstractDatabase database = Vaultcher.getDatabase();
        Pattern pattern = Pattern.compile("name:\\s*(.*?)\\s+target:\\s*(.*)");
        Matcher matcher = pattern.matcher(input.trim());

        if (matcher.matches()) {
            String name = matcher.group(1).trim();
            String target = matcher.group(2).trim();
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);

            if (!database.exists(name)) {
                sender.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
                return;
            }

            database.giveVaultcher(name, targetPlayer);
            sender.sendMessage(MessageKeys.SUCCESSFUL_ADD.getMessage());
        } else sender.sendMessage(MessageKeys.ADD_FORMAT.getMessage());
    }

    @Subcommand("redeem")
    @CommandPermission("vaultcher.redeem")
    @Usage("/vaultcher redeem name: <name>")
    @Description("Redeems the vaultcher.")
    public void redeem(@NotNull Player player, @NotNull @VaultcherCommand String input) {
        AbstractDatabase database = Vaultcher.getDatabase();
        Pattern pattern = Pattern.compile("name:\\s*(.*)");
        Matcher matcher = pattern.matcher(input.trim());

        if (matcher.matches()) {
            String name = matcher.group(1).trim();

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
        } else player.sendMessage(MessageKeys.REDEEM_FORMAT.getMessage());
    }

    @Subcommand("give")
    @CommandPermission("vaultcher.give")
    @Usage("/vaultcher give name: <name> target: <target>")
    @Description("Gives a permission to the vaultcher.")
    public void give(@NotNull Player player, @NotNull String input) {
        AbstractDatabase database = Vaultcher.getDatabase();
        Pattern pattern = Pattern.compile("name:\\s*(.*?)\\s+target:\\s*(.*)");
        Matcher matcher = pattern.matcher(input.trim());

        if (matcher.matches()) {
            String name = matcher.group(1).trim();
            String target = matcher.group(2).trim();
            Player targetPlayer = Bukkit.getPlayer(target);

            if (!database.exists(name)) {
                player.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
                return;
            }

            if (!database.isOwned(name, player)) {
                player.sendMessage(MessageKeys.NOT_AN_OWNER.getMessage());
                return;
            }

            if (targetPlayer == null || !targetPlayer.isOnline()) {
                player.sendMessage(MessageKeys.OFFLINE_PLAYER.getMessage());
                return;
            }

            database.takeVaultcher(name, player.getName(), targetPlayer.getName());
            player.sendMessage(MessageKeys.PLAYER_GIVE.getMessage());
            targetPlayer.sendMessage(MessageKeys.TARGET_GIVE
                    .getMessage()
                    .replace("{player}", player.getName())
                    .replace("{vaultcher}", name));
        } else player.sendMessage(MessageKeys.GIVE_FORMAT.getMessage());
    }

    @Subcommand("referral create")
    @CommandPermission("vaultcher.referral.create")
    @Description("Creates a new unique referral code.")
    public void referralCreate(@NotNull Player player) {
        AbstractDatabase database = Vaultcher.getDatabase();

        if (!database.getReferralCode(player.getName()).isEmpty()) {
            player.sendMessage(MessageKeys.ALREADY_HAVE_REFERRAL.getMessage());
            return;
        }

        String code = database.generateSafeCode();

        database.createReferralCode(player.getName(), code);
        player.sendMessage(MessageKeys.SUCCESSFUL_REFERRAL_CREATE
                .getMessage()
                .replace("{code}", code));
        Vaultcher.getInstance().getServer().getPluginManager().callEvent(new ReferralCreateEvent(player.getName(), code));
    }

    @Subcommand("referral redeem")
    @CommandPermission("vaultcher.referral.redeem")
    @Usage("/vaultcher referral redeem <name>")
    @Description("Redeems the referral code.")
    public void referralRedeem(@NotNull Player player, @NotNull String referral) {
        AbstractDatabase database = Vaultcher.getDatabase();

        if (database.isReferralActivated(player.getName())) {
            player.sendMessage(MessageKeys.ALREADY_ACTIVATED_REFERRAL.getMessage());
            return;
        }

        if (referral.equals(database.getReferralCode(player.getName()))) {
            player.sendMessage(MessageKeys.CANT_ACTIVATE_OWN_REFERRAL.getMessage());
            return;
        }

        if (!database.doesReferralCodeExist(referral)) {
            player.sendMessage(MessageKeys.REFERRAL_NOT_EXISTS.getMessage());
            return;
        }

        database.incrementActivators(referral);
        database.activateReferral(player.getName());
        player.sendMessage(MessageKeys.SUCCESSFUL_REFERRAL_ACTIVATE.getMessage());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ConfigKeys.REFERRAL_COMMAND_CREATOR.getString().replace("{player}", database.getReferralCodeOwner(referral)));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ConfigKeys.REFERRAL_COMMAND_ACTIVATOR.getString().replace("{player}", player.getName()));
        Vaultcher.getInstance().getServer().getPluginManager().callEvent(new ReferralActivateEvent(database.getReferralCodeOwner(referral), player.getName(), referral));
    }
}
