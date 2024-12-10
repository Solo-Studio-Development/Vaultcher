package net.solostudio.vaultcher.utils;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.enums.keys.MessageKeys;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.core.BukkitActor;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.InvalidNumberException;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;

@SuppressWarnings("all")
public final class ExceptionUtils {
    public static void handleSenderNotPlayerException(@NotNull CommandActor actor, @NotNull SenderNotPlayerException context) {
        sendMessage(actor.as(BukkitActor.class), MessageKeys.PLAYER_REQUIRED.getMessage());
    }

    public static void handleInvalidNumberException(@NotNull CommandActor actor, @NotNull InvalidNumberException context) {
        sendMessage(actor.as(BukkitActor.class), MessageKeys.INVALID_NUMBER.getMessage());
    }

    public static void handleNoPermissionException(@NotNull CommandActor actor, @NotNull NoPermissionException context) {
        sendMessage(actor.as(BukkitActor.class), MessageKeys.NO_PERMISSION.getMessage());
    }

    public static void handleInvalidPlayerException(@NotNull CommandActor actor, @NotNull InvalidPlayerException context) {
        sendMessage(actor.as(BukkitActor.class), MessageKeys.INVALID_PLAYER.getMessage());
    }

    public static void handleMissingArgumentException(@NotNull CommandActor actor, @NotNull MissingArgumentException context) {
        sendMessage(actor.as(BukkitActor.class), MessageKeys.MISSING_ARGUMENT
                .getMessage()
                .replace("{usage}", context.getCommand().getUsage()));
    }

    public static boolean handleNonTarget(@NotNull CommandSender sender, @NotNull OfflinePlayer target) {
        if (!Vaultcher.getDatabase().exists(target.getName())) {
            sender.sendMessage(MessageKeys.TARGET_DOESNT_EXISTS.getMessage());
            return false;
        }

        return true;
    }

    private static void sendMessage(@NotNull CommandActor actor, @NotNull String message) {
        actor.as(BukkitActor.class).getSender().sendMessage(message);
    }
}