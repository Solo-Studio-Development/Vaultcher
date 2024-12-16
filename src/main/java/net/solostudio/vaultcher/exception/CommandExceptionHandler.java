package net.solostudio.vaultcher.exception;

import net.solostudio.vaultcher.enums.keys.MessageKeys;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.core.BukkitActor;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.exception.InvalidNumberException;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@SuppressWarnings("all")
public final class CommandExceptionHandler {

    private static final Map<Class<? extends RuntimeException>, BiConsumer<CommandActor, RuntimeException>> exceptionHandlers = new HashMap<>();

    static {
        registerHandler(SenderNotPlayerException.class, (actor, exception) -> sendMessage(actor, MessageKeys.PLAYER_REQUIRED.getMessage()));
        registerHandler(InvalidNumberException.class, (actor, exception) -> sendMessage(actor, MessageKeys.INVALID_NUMBER.getMessage()));
        registerHandler(NoPermissionException.class, (actor, exception) -> sendMessage(actor, MessageKeys.NO_PERMISSION.getMessage()));
        registerHandler(InvalidPlayerException.class, (actor, exception) -> sendMessage(actor, MessageKeys.INVALID_PLAYER.getMessage()));
        registerHandler(MissingArgumentException.class, (actor, exception) -> sendMessage(actor,
                MessageKeys.MISSING_ARGUMENT.getMessage().replace("{usage}", exception.getCommand().getUsage())));
    }

    public static void handleException(@NotNull CommandActor actor, @NotNull RuntimeException exception) {
        BiConsumer<CommandActor, RuntimeException> handler = exceptionHandlers.get(exception.getClass());

        if (handler != null) handler.accept(actor, exception);
    }

    private static void sendMessage(@NotNull CommandActor actor, @NotNull String message) {
        if (actor instanceof BukkitActor bukkitActor) bukkitActor.getSender().sendMessage(message);
    }

    private static <T extends RuntimeException> void registerHandler(Class<T> exceptionClass, BiConsumer<CommandActor, T> handler) {
        exceptionHandlers.put(exceptionClass, (actor, exception) -> handler.accept(actor, exceptionClass.cast(exception)));
    }
}
