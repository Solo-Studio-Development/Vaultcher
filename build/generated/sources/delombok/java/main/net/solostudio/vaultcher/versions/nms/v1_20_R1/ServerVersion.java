package net.solostudio.vaultcher.versions.nms.v1_20_R1;

import net.solostudio.vaultcher.interfaces.ServerVersionSupport;
import net.solostudio.vaultcher.utils.LoggerUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ServerVersion implements ServerVersionSupport {

    @Contract(pure = true)
    public ServerVersion(@NotNull Plugin plugin) {
        LoggerUtils.info("### Loading support for version 1.20 and 1.20.1... ###");
        LoggerUtils.info("### Support for 1.20 and 1.20.1 is loaded! ###");
    }

    @Override
    public String getName() {
        return "1.20_R1";
    }

    @Override
    public boolean isSupported() {
        return true;
    }
}
