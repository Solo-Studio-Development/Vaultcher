package net.solostudio.vaultcher.commands;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.annotations.DatabasePlayers;
import net.solostudio.vaultcher.annotations.VaultcherCommand;
import net.solostudio.vaultcher.enums.VersionTypes;
import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import net.solostudio.vaultcher.enums.keys.MessageKeys;
import net.solostudio.vaultcher.events.*;
import net.solostudio.vaultcher.hooks.PlaceholderAPI;
import net.solostudio.vaultcher.hooks.Webhook;
import net.solostudio.vaultcher.interfaces.VaultcherDatabase;
import net.solostudio.vaultcher.managers.MenuController;
import net.solostudio.vaultcher.managers.VaultcherData;
import net.solostudio.vaultcher.menu.menus.NavigationMenu;
import net.solostudio.vaultcher.update.SpigotUpdateFetcher;
import net.solostudio.vaultcher.utils.EventUtils;
import net.solostudio.vaultcher.utils.VaultcherUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.orphan.OrphanCommand;

@SuppressWarnings("deprecation")
public class CommandVaultcher implements OrphanCommand {
    @CommandPlaceholder
    public void defaultCommand(@NotNull CommandSender sender) {
        help(sender);
    }

    @Subcommand("help")
    public void help(@NotNull CommandSender sender) {
        if (sender.hasPermission("vaultcher.admin")) {
            MessageKeys.ADMIN_HELP
                    .getMessages()
                    .forEach(sender::sendMessage);
        } else MessageKeys.PLAYER_HELP
                    .getMessages()
                    .forEach(sender::sendMessage);
    }

    @Subcommand("about")
    @CommandPermission("vaultcher.about")
    public void about(@NotNull CommandSender sender) {
        boolean isRegistered = PlaceholderAPI.isRegistered;

        MessageKeys.ABOUT_MESSAGE.getMessages().stream()
                .map(message -> {
                    String modifiedMessage = isRegistered ? me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((Player) sender, message) : message;

                    return modifiedMessage
                            .replace("[currentPluginVersion]", "v" + Vaultcher.getInstance().getDescription().getVersion())
                            .replace("[latestPluginVersion]", "v" + SpigotUpdateFetcher.getLatestVersion())
                            .replace("[serverVersion]", String.valueOf(VersionTypes.getServerVersion()))
                            .replace("[databaseType]", ConfigKeys.DATABASE.getString().toUpperCase())
                            .replace("[language]", ConfigKeys.LANGUAGE.getString().toUpperCase())
                            .replace("[author]", "User-19fff")
                            .replace("[enabledWebhooks]", String.valueOf(Webhook.countEnabledWebhooks()))
                            .replace("[isDatabaseConnected]", Vaultcher.getDatabase().isConnected() ? MessageKeys.TRUE.getMessage() : MessageKeys.FALSE.getMessage())
                            .replace("[vaultchersCreated]", String.valueOf(Vaultcher.getDatabase().countVaultchers()));
                })
                .forEach(sender::sendMessage);
    }

    @Subcommand("reload")
    @Description("Reloads the plugin.")
    @CommandPermission("vaultcher.reload")
    public void reload(@NotNull CommandSender sender) {
        Vaultcher.getInstance().getLanguage().reload();
        Vaultcher.getInstance().getConfiguration().reload();
        Vaultcher.getInstance().getWebhookFile().reload();
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
    @Usage("/vaultcher create --name 'name' --uses uses --command 'command'")
    @Description("Creates a new vaultcher.")
    public void create(@NotNull CommandSender sender, @NotNull @Flag(shorthand = 'a') String name, @Flag(shorthand = 'b') int uses, @NotNull @Flag(shorthand = 'c') String command) {
        VaultcherDatabase vaultcherDatabase = Vaultcher.getDatabase();

        if (vaultcherDatabase.exists(name)) {
            sender.sendMessage(MessageKeys.ALREADY_EXISTS.getMessage());
            return;
        }

        if (uses < 0) {
            sender.sendMessage(MessageKeys.CANT_BE_NEGATIVE.getMessage());
            return;
        }

        VaultcherData vaultcher = new VaultcherData(name, command, uses);
        vaultcherDatabase.createVaultcher(vaultcher.vaultcherName(), vaultcher.command(), vaultcher.uses());
        sender.sendMessage(MessageKeys.CREATED.getMessage());
        Vaultcher.getInstance().getServer().getPluginManager().callEvent(new VaultcherCreateEvent(EventUtils.handleConsoleEvent(sender).orElse(null), name, command, uses));
    }

    @Subcommand("delete")
    @CommandPermission("vaultcher.delete")
    @Usage("vaultcher delete --name 'name'")
    @Description("Deletes the vaultcher.")
    public void delete(@NotNull CommandSender sender, @NotNull @VaultcherCommand @Flag(shorthand = 'd') String name) {
        VaultcherDatabase vaultcherDatabase = Vaultcher.getDatabase();

        if (!vaultcherDatabase.exists(name)) {
            sender.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
            return;
        }

        vaultcherDatabase.deleteVaultcher(name);
        sender.sendMessage(MessageKeys.DELETED.getMessage());
        Vaultcher.getInstance().getServer().getPluginManager().callEvent(new VaultcherDeleteEvent(EventUtils.handleConsoleEvent(sender).orElse(null), name));
    }

    @Subcommand("edituse")
    @CommandPermission("vaultcher.edituse")
    @Usage("/vaultcher edituse --name 'name' --nuse <new use>")
    @Description("Edits the uses of the vaultcher.")
    public void edituse(@NotNull CommandSender sender, @NotNull @VaultcherCommand @Flag(shorthand = 'e') String name, @Flag(shorthand = 'f') int nuse) {
        VaultcherDatabase vaultcherDatabase = Vaultcher.getDatabase();

        if (!vaultcherDatabase.exists(name)) {
            sender.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
            return;
        }

        if (nuse < 0) {
            sender.sendMessage(MessageKeys.CANT_BE_NEGATIVE.getMessage());
            return;
        }

        Vaultcher.getInstance().getServer().getPluginManager().callEvent(new VaultcherUseEditEvent(name, nuse));
        vaultcherDatabase.changeUses(name, nuse);
        sender.sendMessage(MessageKeys.EDIT_USES.getMessage());
    }

    @Subcommand("editname")
    @CommandPermission("vaultcher.editname")
    @Usage("/vaultcher editname --name 'name' --nname 'new name'")
    @Description("Edits the name of the vaultcher.")
    public void editname(@NotNull CommandSender sender, @NotNull @VaultcherCommand @Flag(shorthand = 'g') String name, @Flag(shorthand = 'h') String nname) {
        VaultcherDatabase vaultcherDatabase = Vaultcher.getDatabase();

        if (!vaultcherDatabase.exists(name)) {
            sender.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
            return;
        }

        Vaultcher.getInstance().getServer().getPluginManager().callEvent(new VaultcherNameEditEvent(name, nname));
        vaultcherDatabase.changeName(name, nname);
        sender.sendMessage(MessageKeys.EDIT_NAME.getMessage());
    }

    @Subcommand("editcommand")
    @CommandPermission("vaultcher.editcommand")
    @Usage("/vaultcher editcommand --name 'name' --ncommand 'new command'")
    @Description("Edits the command of the vaultcher.")
    public void editcommand(@NotNull CommandSender sender, @NotNull @VaultcherCommand @Flag(shorthand = 'i') String name, @Flag(shorthand = 'k') String ncommand) {
        VaultcherDatabase vaultcherDatabase = Vaultcher.getDatabase();

        if (!vaultcherDatabase.exists(name)) {
            sender.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
            return;
        }

        Vaultcher.getInstance().getServer().getPluginManager().callEvent(new VaultcherCommandEditEvent(name, ncommand));
        vaultcherDatabase.changeCommand(name, ncommand);
        sender.sendMessage(MessageKeys.EDIT_CMD.getMessage());
    }

    @Subcommand("add")
    @CommandPermission("vaultcher.add")
    @Usage("/vaultcher add --name 'name' --target 'target'")
    @Description("Adds a permission to the vaultcher.")
    public void add(@NotNull CommandSender sender, @NotNull @VaultcherCommand @Flag(shorthand = 'l') String name, @NotNull @DatabasePlayers @Flag(shorthand = 'm') String target) {
        VaultcherDatabase vaultcherDatabase = Vaultcher.getDatabase();
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);

        if (!vaultcherDatabase.exists(name)) {
            sender.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
            return;
        }

        vaultcherDatabase.giveVaultcher(name, targetPlayer);
        sender.sendMessage(MessageKeys.SUCCESSFUL_ADD.getMessage());
    }

    @Subcommand("redeem")
    @CommandPermission("vaultcher.redeem")
    @Usage("/vaultcher redeem --name 'name'")
    @Description("Redeems the vaultcher.")
    public void redeem(@NotNull Player player, @NotNull @VaultcherCommand @Flag(shorthand = 'n') String name) {
        VaultcherDatabase vaultcherDatabase = Vaultcher.getDatabase();

        if (!vaultcherDatabase.exists(name)) {
            player.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
            return;
        }

        if (vaultcherDatabase.isUsesZero(name)) {
            player.sendMessage(MessageKeys.USES_ZERO.getMessage());
            return;
        }

        if (!vaultcherDatabase.isOwned(name, player)) {
            player.sendMessage(MessageKeys.NOT_AN_OWNER.getMessage());
            return;
        }

        VaultcherUtils.redeemVaultcher(player, name);
    }

    @Subcommand("give")
    @CommandPermission("vaultcher.give")
    @Usage("/vaultcher give --name 'name' --target 'target'")
    @Description("Gives a permission to the vaultcher.")
    public void give(@NotNull Player player, @NotNull @VaultcherCommand @Flag(shorthand = 'o') String name, @NotNull @DatabasePlayers @Flag(shorthand = 'p') String target) {
        VaultcherDatabase vaultcherDatabase = Vaultcher.getDatabase();
        Player targetPlayer = Bukkit.getPlayer(target);

        if (!vaultcherDatabase.exists(name)) {
            player.sendMessage(MessageKeys.NOT_EXISTS.getMessage());
            return;
        }

        if (!vaultcherDatabase.isOwned(name, player)) {
            player.sendMessage(MessageKeys.NOT_AN_OWNER.getMessage());
            return;
        }

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            player.sendMessage(MessageKeys.OFFLINE_PLAYER.getMessage());
            return;
        }

        vaultcherDatabase.takeVaultcher(name, player.getName());
        vaultcherDatabase.giveVaultcher(name, targetPlayer);
        player.sendMessage(MessageKeys.PLAYER_GIVE.getMessage());
        targetPlayer.sendMessage(MessageKeys.TARGET_GIVE
                .getMessage()
                .replace("{player}", player.getName())
                .replace("{vaultcher}", name));
    }

    @Subcommand("referral create")
    @CommandPermission("vaultcher.referral.create")
    @Description("Creates a new unique referral code.")
    public void referralCreate(@NotNull Player player) {
        VaultcherDatabase vaultcherDatabase = Vaultcher.getDatabase();

        if (!vaultcherDatabase.getReferralCode(player.getName()).isEmpty()) {
            player.sendMessage(MessageKeys.ALREADY_HAVE_REFERRAL.getMessage());
            return;
        }

        String code = vaultcherDatabase.generateSafeCode();

        vaultcherDatabase.createReferralCode(player.getName(), code);
        player.sendMessage(MessageKeys.SUCCESSFUL_REFERRAL_CREATE
                .getMessage()
                .replace("{code}", code));
        Vaultcher.getInstance().getServer().getPluginManager().callEvent(new ReferralCreateEvent(player.getName(), code));
    }

    @Subcommand("referral redeem")
    @CommandPermission("vaultcher.referral.redeem")
    @Usage("/vaultcher referral redeem --referral 'name'")
    @Description("Redeems the referral code.")
    public void referralRedeem(@NotNull Player player, @NotNull @Flag(shorthand = 'q') String referral) {
        VaultcherDatabase vaultcherDatabase = Vaultcher.getDatabase();

        if (vaultcherDatabase.isReferralActivated(player.getName())) {
            player.sendMessage(MessageKeys.ALREADY_ACTIVATED_REFERRAL.getMessage());
            return;
        }

        if (referral.equals(vaultcherDatabase.getReferralCode(player.getName()))) {
            player.sendMessage(MessageKeys.CANT_ACTIVATE_OWN_REFERRAL.getMessage());
            return;
        }

        if (!vaultcherDatabase.doesReferralCodeExist(referral)) {
            player.sendMessage(MessageKeys.REFERRAL_NOT_EXISTS.getMessage());
            return;
        }

        vaultcherDatabase.incrementActivators(referral);
        vaultcherDatabase.activateReferral(player.getName());
        player.sendMessage(MessageKeys.SUCCESSFUL_REFERRAL_ACTIVATE.getMessage());
        ConfigKeys.REFERRAL_COMMAND_CREATOR.getList().forEach(currentCommand -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), currentCommand.replace("{player}", vaultcherDatabase.getReferralCodeOwner(referral))));
        ConfigKeys.REFERRAL_COMMAND_ACTIVATOR.getList().forEach(currentCommand -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), currentCommand.replace("{player}", player.getName())));
        Vaultcher.getInstance().getServer().getPluginManager().callEvent(new ReferralActivateEvent(vaultcherDatabase.getReferralCodeOwner(referral), player.getName(), referral));
    }
}