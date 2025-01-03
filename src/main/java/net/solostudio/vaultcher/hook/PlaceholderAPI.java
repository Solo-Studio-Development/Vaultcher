package net.solostudio.vaultcher.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.solostudio.vaultcher.Vaultcher;
import net.solostudio.vaultcher.database.AbstractDatabase;
import net.solostudio.vaultcher.enums.keys.ConfigKeys;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class PlaceholderAPI {
    public static boolean isRegistered = false;

    public static void registerHook() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderIntegration().register();
            isRegistered = true;
        }
    }

    private static class PlaceholderIntegration extends PlaceholderExpansion {
        @Override
        public @NotNull String getIdentifier() {
            return "v";
        }

        @Override
        public @NotNull String getAuthor() {
            return "User-19fff";
        }

        @Override
        public @NotNull String getVersion() {
            return Vaultcher.getInstance().getDescription().getVersion();
        }

        @Override
        public boolean canRegister() {
            return true;
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onPlaceholderRequest(@NotNull Player player, @NotNull String params) {
            AbstractDatabase database = Vaultcher.getDatabase();

            return switch (params) {
                case "activators" -> String.valueOf(database.getActivatorsFromPlayer(player.getName()));
                case "code" ->
                        database.getReferralCode(player.getName()).isEmpty() ? ConfigKeys.PLACEHOLDER_NO_CODE.getString() : database.getReferralCode(player.getName());
                case "is_activated" ->
                        database.isReferralActivated(player.getName()) ? ConfigKeys.PLACEHOLDER_YES.getString() : ConfigKeys.PLACEHOLDER_NO.getString();

                default -> "";
            };
        }
    }
}
