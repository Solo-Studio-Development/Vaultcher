package net.solostudio.vaultcher.utils;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.commands.CommandVaultcher;
import net.solostudio.vaultcher.exception.CommandExceptionHandler;
import net.solostudio.vaultcher.managers.VaultcherData;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.reflections.Reflections;
import revxrsal.commands.bukkit.BukkitLamp;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

public class RegisterUtils {
    private static final String BASE_PACKAGE = "net.solostudio.vaultcher";

    public static void registerListeners() {
        LoggerUtils.info("### Registering listeners... ###");

        AtomicInteger count = new AtomicInteger();

        new Reflections(BASE_PACKAGE)
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

        var lamp = BukkitLamp.builder(Vaultcher.getInstance())
                .exceptionHandler(new CommandExceptionHandler())
                .suggestionProviders(providers -> {
                    providers.addProvider(String.class, context -> Vaultcher.getDatabase().getEveryVaultcher().stream().map(VaultcherData::vaultcherName).toList());
                })
                .build();

        lamp.register(new CommandVaultcher());

        LoggerUtils.info("### Successfully registered exception handlers... ###");
    }
}
