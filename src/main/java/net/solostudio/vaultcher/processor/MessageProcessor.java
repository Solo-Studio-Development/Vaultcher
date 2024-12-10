package net.solostudio.vaultcher.processor;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class MessageProcessor {
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F\\d]{6}");

    public static @NotNull String process(@Nullable String message) {
        if (message == null) return "";

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String hexCode = matcher.group();
            StringBuilder builder = new StringBuilder();

            for (char c : hexCode.substring(1).toCharArray()) {
                builder.append("&").append(c);
            }

            matcher.appendReplacement(result, builder.toString());
        }

        matcher.appendTail(result);

        return ChatColor.translateAlternateColorCodes('&', result.toString());
    }
}
