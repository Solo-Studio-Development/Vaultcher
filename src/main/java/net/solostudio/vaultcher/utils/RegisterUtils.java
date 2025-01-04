package net.solostudio.vaultcher.utils;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.annotations.DatabasePlayers;
import net.solostudio.vaultcher.annotations.VaultcherCommand;
import net.solostudio.vaultcher.commands.CommandVaultcher;
import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import net.solostudio.vaultcher.exception.CommandExceptionHandler;
import net.solostudio.vaultcher.interfaces.VaultcherDatabase;
import net.solostudio.vaultcher.listeners.DatabaseListener;
import net.solostudio.vaultcher.listeners.MenuListener;
import net.solostudio.vaultcher.listeners.WebhookListener;
import net.solostudio.vaultcher.managers.VaultcherData;
import org.bukkit.Bukkit;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.orphan.Orphans;

import java.util.Objects;

public class RegisterUtils {
    public static void registerListeners() {
        LoggerUtils.info("### Registering listeners... ###");

        Bukkit.getPluginManager().registerEvents(new DatabaseListener(), Vaultcher.getInstance());
        Bukkit.getPluginManager().registerEvents(new WebhookListener(), Vaultcher.getInstance());
        Bukkit.getPluginManager().registerEvents(new MenuListener(), Vaultcher.getInstance());

        LoggerUtils.info("### Successfully registered 3 listener. ###");
    }

    public static void registerCommands() {
        VaultcherDatabase vaultcherDatabase = Vaultcher.getDatabase();
        LoggerUtils.info("### Registering commands... ###");

        var lamp = BukkitLamp.builder(Vaultcher.getInstance())
                .exceptionHandler(new CommandExceptionHandler())
                .suggestionProviders(providers -> {
                    providers.addProviderForAnnotation(VaultcherCommand.class, vaultcherCommand -> context -> {

                        if (context.actor().sender().hasPermission("vaultcher.admin")) {
                            return vaultcherDatabase.getEveryVaultcher()
                                    .stream()
                                    .map(VaultcherData::vaultcherName)
                                    .toList();
                        } else {
                            return vaultcherDatabase.getVaultchers(Objects.requireNonNull(context.actor().asPlayer()))
                                    .stream()
                                    .map(VaultcherData::vaultcherName)
                                    .toList();
                        }
                    });
                })

                .suggestionProviders(providers -> {
                    providers.addProviderForAnnotation(DatabasePlayers.class, databasePlayers -> context -> vaultcherDatabase.getEveryPlayerInDatabase()
                            .stream()
                            .toList());
                })

                .build();

        lamp.register(Orphans.path(ConfigKeys.ALIASES.getList().toArray(String[]::new)).handler(new CommandVaultcher()));

        LoggerUtils.info("### Successfully registered exception handlers... ###");
    }
}