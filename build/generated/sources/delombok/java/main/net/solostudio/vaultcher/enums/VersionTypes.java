package net.solostudio.vaultcher.enums;

import net.solostudio.vaultcher.utils.LoggerUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum VersionTypes {
    UNKNOWN,
    v1_20_R1,
    v1_20_R2,
    v1_20_R3,
    v1_20_R4,
    v1_20_R5,
    v1_20_R6,
    v1_21_R1,
    v1_21_R2,
    v1_21_R3,
    v1_21_R4;

    private static VersionTypes serverVersion;

    static {
        String bukkitVersion = Bukkit.getVersion();
        Pattern pattern = Pattern.compile("\\(MC: (\\d+)\\.(\\d+)(?:\\.(\\d+))?\\)");
        Matcher matcher = pattern.matcher(bukkitVersion);

        if (matcher.find()) {
            try {
                int major = Integer.parseInt(matcher.group(1));
                int minor = Integer.parseInt(matcher.group(2));
                int patch = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;

                serverVersion = determineVersion(major, minor, patch);
            } catch (NumberFormatException exception) {
                serverVersion = UNKNOWN;
            }
        } else {
            serverVersion = UNKNOWN;
            LoggerUtils.error("### Could not determine the server version from Bukkit version string: {} ###", bukkitVersion);
        }
    }

    public static VersionTypes determineVersion(int major, int minor, int patch) {
        if (major == 1) {
            return switch (minor) {
                case 20 -> switch (patch) {
                    case 1 -> v1_20_R1; // 1.20.1 and 1.20
                    case 2 -> v1_20_R2; // 1.20.2
                    case 3, 4 -> v1_20_R3; // 1.20.3 and 1.20.4
                    case 5, 6 -> v1_20_R4; // 1.20.5 and 1.20.6
                    default -> UNKNOWN;
                };

                case 21 -> switch (patch) {
                    case 1 -> v1_21_R1; // 1.21.1 and 1.21
                    case 2 -> v1_21_R2; // 1.21.2
                    case 3, 4 -> v1_21_R3; // 1.21.3 and 1.21.4
                    default -> UNKNOWN;
                };

                default -> UNKNOWN;
            };
        }
        return UNKNOWN;
    }

    public static VersionTypes getCurrentVersion() {
        LoggerUtils.info("Current Minecraft version: " + serverVersion);
        return serverVersion;
    }

    public static boolean isServerVersion(@NotNull VersionTypes version) {
        return serverVersion == version;
    }

    public static boolean isServerVersion(@NotNull VersionTypes... versions) {
        return ArrayUtils.contains(versions, serverVersion);
    }

    public static boolean isServerVersionAbove(@NotNull VersionTypes version) {
        return serverVersion.ordinal() > version.ordinal();
    }

    public static boolean isServerVersionAtLeast(@NotNull VersionTypes version) {
        return serverVersion.ordinal() >= version.ordinal();
    }

    public static boolean isServerVersionAtOrBelow(@NotNull VersionTypes version) {
        return serverVersion.ordinal() <= version.ordinal();
    }

    public static boolean isServerVersionBelow(@NotNull VersionTypes version) {
        return serverVersion.ordinal() < version.ordinal();
    }

    public boolean isLessThan(@NotNull VersionTypes other) {
        return this.ordinal() < other.ordinal();
    }

    public boolean isAtOrBelow(@NotNull VersionTypes other) {
        return this.ordinal() <= other.ordinal();
    }

    public boolean isGreaterThan(@NotNull VersionTypes other) {
        return this.ordinal() > other.ordinal();
    }

    public boolean isAtLeast(@NotNull VersionTypes other) {
        return this.ordinal() >= other.ordinal();
    }
}
