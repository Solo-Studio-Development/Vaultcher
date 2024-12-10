package net.solostudio.vaultcher.utils;

import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.enums.VersionTypes;
import net.solostudio.vaultcher.versions.VersionSupport;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.solostudio.vaultcher.enums.VersionTypes.determineVersion;

public class StartingUtils {
    private static boolean isSupported;

    public static void registerListenersAndCommands() {
        RegisterUtils.registerListeners();
        RegisterUtils.registerCommands();
    }

    public static void saveResourceIfNotExists(@NotNull String resourcePath) {
        if (!new File(Vaultcher.getInstance().getDataFolder(), resourcePath).exists())
            Vaultcher.getInstance().saveResource(resourcePath, false);
    }

    public static void checkVM() {
        if (getVMVersion() < 17) {
            Bukkit.getPluginManager().disablePlugin(Vaultcher.getInstance());
            LoggerUtils.error("### Wrong VM version! ###");
            return;
        }

        if (!isSupported) {
            LoggerUtils.error("### This version of Vaultcher is not supported on this server version. ###");
            LoggerUtils.error("### Please consider updating your server version to a newer version. ###");
            Bukkit.getPluginManager().disablePlugin(Vaultcher.getInstance());
        }
    }

    public static void checkVersion() {
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        } catch (Exception ignored) {
            isSupported = false;
            LoggerUtils.error("### SpigotConfig class not found. This might indicate an unsupported server. ###");
            return;
        }

        try {
            LoggerUtils.info("### Detected Bukkit version string: {} ###", Bukkit.getVersion());

            Pattern pattern = Pattern.compile("\\(MC: (\\d{1,2})\\.(\\d{1,2})(?:\\.(\\d{1,2}))?\\)");
            Matcher matcher = pattern.matcher(Bukkit.getVersion());

            if (matcher.find()) {
                int majorVersion = Integer.parseInt(matcher.group(1));
                int minorVersion = Integer.parseInt(matcher.group(2));
                int patchVersion = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
                VersionTypes version = determineVersion(majorVersion, minorVersion, patchVersion);

                if (version == VersionTypes.UNKNOWN) {
                    isSupported = false;

                    LoggerUtils.error("### Unknown Minecraft version: {}.{}.{} ###", majorVersion, minorVersion, patchVersion);
                    return;
                }

                isSupported = new VersionSupport(version).getVersionSupport() != null;
            }
        } catch (Exception exception) {
            isSupported = false;

            LoggerUtils.error("### Exception occurred during version check: {} ###", exception.getMessage());
        }

        if (!isSupported) {
            LoggerUtils.error("### This version of Vaultcher is not supported on this server version. ###");
            LoggerUtils.error("### Please consider updating your server version to a newer version. ###");
            Bukkit.getPluginManager().disablePlugin(Vaultcher.getInstance());
        }
    }


    static int getVMVersion() {
        String javaVersion = System.getProperty("java.version");
        Matcher matcher = Pattern.compile("(?:1\\.)?(\\d+)").matcher(javaVersion);

        if (!matcher.find()) return -1;

        String version = matcher.group(1);

        try {
            return Integer.parseInt(version);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}
