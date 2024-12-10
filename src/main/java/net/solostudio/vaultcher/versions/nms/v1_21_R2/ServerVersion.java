package net.solostudio.vaultcher.versions.nms.v1_21_R2;

import net.solostudio.vaultcher.interfaces.ServerVersionSupport;
import net.solostudio.vaultcher.utils.LoggerUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ServerVersion implements ServerVersionSupport {
    @Contract(pure = true)
    public ServerVersion(@NotNull Plugin plugin) {
        LoggerUtils.info("### Loading support for version 1.21.2... ###");
        LoggerUtils.info("### Support for 1.21.2 is loaded! ###");
    }

    @Override
    public String getName() {
        return "1.21_R2";
    }

    @Override
    public boolean isSupported() {
        return true;
    }
}
