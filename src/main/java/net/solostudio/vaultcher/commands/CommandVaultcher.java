package net.solostudio.vaultcher.commands;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.database.AbstractDatabase;
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
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        new NavigationMenu(MenuController.getMenuUtils(player)).open();
    }

    @Subcommand("create")
    @CommandPermission("vaultcher.create")
    public void create(@NotNull CommandSender sender, @NotNull String input) {
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
    public void delete(@NotNull CommandSender sender, @NotNull String input) {
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
    public void edituse(@NotNull CommandSender sender, @NotNull String input) {
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
    public void editname(@NotNull CommandSender sender, @NotNull String input) {
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
    public void editcommand(@NotNull CommandSender sender, @NotNull String input) {
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
    public void add(@NotNull CommandSender sender, @NotNull String input) {
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
    public void redeem(@NotNull Player player, @NotNull String input) {
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
        } else {
            player.sendMessage(MessageKeys.REDEEM_FORMAT.getMessage());
        }
    }

    @Subcommand("give")
    @CommandPermission("vaultcher.give")
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
    public void referralCreate(@NotNull Player player) {
        AbstractDatabase database = Vaultcher.getDatabase();

        // Ha már van kódja, értesítjük a játékost, és kilépünk
        if (!database.getReferralCode(player.getName()).isEmpty()) {
            player.sendMessage("Neked már van referral kódod!");
            return;
        }

        // Új referral kód generálása és hozzárendelése a játékoshoz
        String code = database.generateSafeCode();
        database.createReferralCode(player.getName(), code);
        player.sendMessage("Sikeresen létrehoztad a referral kódodat: " + code);
    }

    @Subcommand("referral redeem")
    @CommandPermission("vaultcher.referral.redeem")
    public void referralRedeem(@NotNull Player player, @NotNull String referral) {
        AbstractDatabase database = Vaultcher.getDatabase();

        // Ellenőrizzük, hogy a játékos már aktivált-e egy referral kódot
        if (database.isReferralActivated(player.getName())) {
            player.sendMessage("Te már aktiváltál egy referral kódot korábban!");
            return;
        }

        // Ellenőrizzük, hogy a játékos nem a saját kódját próbálja aktiválni
        String playerCode = database.getReferralCode(player.getName());
        if (referral.equals(playerCode)) {
            player.sendMessage("Nem aktiválhatod a saját referral kódodat!");
            return;
        }

        // Ellenőrizzük, hogy a kód létezik-e az adatbázisban
        if (!database.doesReferralCodeExist(referral)) {
            player.sendMessage("Ez a referral kód nem létezik!");
            return;
        }

        // Aktiváljuk a referral kódot a játékosnak
        if (database.activateReferral(player.getName())) {
            // Növeljük a kód tulajdonosának aktivátor számlálóját
            database.incrementActivators(referral);

            player.sendMessage("Sikeresen aktiváltad a referral kódot: " + referral);
        } else {
            player.sendMessage("Hiba történt a kód aktiválása közben.");
        }
    }

}
