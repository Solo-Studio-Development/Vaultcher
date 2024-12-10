package net.solostudio.vaultcher.utils;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.commands.CommandVaultcher;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.exception.InvalidPlayerException;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.exception.InvalidNumberException;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

public class RegisterUtils {
    public static void registerListeners() {
        LoggerUtils.info("### Registering listeners... ###");

        AtomicInteger count = new AtomicInteger();

        new Reflections("net.solostudio.vaultcher.listeners")
                .getSubTypesOf(Listener.class)
                .forEach(listenerClass -> {
                    try {
                        Bukkit.getServer().getPluginManager().registerEvents(listenerClass.getDeclaredConstructor().newInstance(), Vaultcher.getInstance());
                        count.getAndIncrement();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                        LoggerUtils.error(exception.getMessage());
                    }
                });

        LoggerUtils.info("### Successfully registered {} listener. ###", count.get());
    }

    public static void registerCommands() {
        LoggerUtils.info("### Registering commands... ###");

        BukkitCommandHandler handler = BukkitCommandHandler.create(Vaultcher.getInstance());
        handler.register(new CommandVaultcher());

        LoggerUtils.info("### Successfully registered {} command(s). ###", handler.getCommands().size());

        LoggerUtils.info("### Registering exception handlers... ###");
        handler.registerExceptionHandler(SenderNotPlayerException.class, ExceptionUtils::handleSenderNotPlayerException);
        handler.registerExceptionHandler(InvalidNumberException.class, ExceptionUtils::handleInvalidNumberException);
        handler.registerExceptionHandler(NoPermissionException.class, ExceptionUtils::handleNoPermissionException);
        handler.registerExceptionHandler(MissingArgumentException.class, ExceptionUtils::handleMissingArgumentException);
        handler.registerExceptionHandler(InvalidPlayerException.class, ExceptionUtils::handleInvalidPlayerException);
        handler.registerBrigadier();
        LoggerUtils.info("### Successfully registered exception handlers... ###");
    }
}
